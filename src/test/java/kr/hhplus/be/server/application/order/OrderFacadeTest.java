package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderFacadeTest {

    private static final Logger log = LoggerFactory.getLogger(OrderFacadeTest.class);
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;


    private Long USER_ID;
    private Product PRODUCT;
    private ProductOption OPTION1;
    private ProductOption OPTION2;
    private ProductOption OPTION3;
    private List<OrderCriteria.OrderItem> ORDER_ITEMS;
    Coupon COUPON;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        PRODUCT = productRepository.save(new Product("농심", "라면"));
        OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "신라면", 1000L, 100));
        OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "너구리", 2000L, 100));
        OPTION3 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "짜파게티", 3000L, 100));

        ORDER_ITEMS = List.of(
                new OrderCriteria.OrderItem(OPTION1.getId(), 30),
                new OrderCriteria.OrderItem(OPTION2.getId(), 20),
                new OrderCriteria.OrderItem(OPTION3.getId(), 10)
        );

        COUPON = couponRepository.save(new Coupon(1000L, 10));
    }

    @Test
    @DisplayName("[성공] 주문 - 쿠폰 미사용")
    void order_ok() {

        // Arrange
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, null);

        // Act
        OrderResult.Create result = orderFacade.order(criteria);

        // Assert
        Order order = orderRepository.findById(result.orderId()).get();
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getTotalAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 10L);
        assertThat(order.getDiscountAmount()).isEqualTo(0L);
        assertThat(order.getPaymentAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 10L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        ProductOption productOption1 = productOptionRepository.findById(OPTION1.getId()).get();
        OrderItem orderItem1 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption1.getId()).get();
        assertThat(orderItem1.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem1.getQuantity()).isEqualTo(30);

        ProductOption productOption2 = productOptionRepository.findById(OPTION2.getId()).get();
        OrderItem orderItem2 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption2.getId()).get();
        assertThat(orderItem2.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem2.getQuantity()).isEqualTo(20);

        ProductOption productOption3 = productOptionRepository.findById(OPTION3.getId()).get();
        OrderItem orderItem3 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption3.getId()).get();
        assertThat(orderItem3.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem3.getQuantity()).isEqualTo(10);

        Payment payment = paymentRepository.findByOrderId(order.getId()).get();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(order.getPaymentAmount());
    }

    @Test
    @DisplayName("[성공] 주문 - 쿠폰 사용")
     void orderWithCoupon_ok() {

        // Arrange
        issuedCouponRepository.save(new IssuedCoupon(USER_ID, COUPON.getId()));
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, COUPON.getId());

        // Act
        OrderResult.Create result = orderFacade.order(criteria);

        // Assert
        Order order = orderRepository.findById(result.orderId()).get();
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getTotalAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 10L);
        assertThat(order.getDiscountAmount()).isEqualTo(1000L);
        assertThat(order.getPaymentAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 10L - 1000L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        ProductOption productOption1 = productOptionRepository.findById(OPTION1.getId()).get();
        OrderItem orderItem1 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption1.getId()).get();
        assertThat(orderItem1.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem1.getQuantity()).isEqualTo(30);

        ProductOption productOption2 = productOptionRepository.findById(OPTION2.getId()).get();
        OrderItem orderItem2 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption2.getId()).get();
        assertThat(orderItem2.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem2.getQuantity()).isEqualTo(20);

        ProductOption productOption3 = productOptionRepository.findById(OPTION3.getId()).get();
        OrderItem orderItem3 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption3.getId()).get();
        assertThat(orderItem3.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem3.getQuantity()).isEqualTo(10);

        Payment payment = paymentRepository.findByOrderId(order.getId()).get();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(order.getPaymentAmount());

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON.getId()).get();
        assertThat(issuedCoupon.getStatus()).isEqualTo(CouponStatus.USED);
        assertThat(issuedCoupon.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("[실패] 주문 - 상품 없음(NOT_FOUND)")
    void order_notFound() {

        // Arrange
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, 999L, ORDER_ITEMS, null);

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> orderFacade.order(criteria));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("[실패] 주문 - 유효하지 않은 쿠폰(NOT_FOUND)")
    void orderWithCoupon_couponNotFound() {

        // Arrange
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, 999L);

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> orderFacade.order(criteria));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("[실패] 주문 - 발급되지 않은 쿠폰(NOT_FOUND)")
    void orderWithCoupon_issuedCouponNotFound() {

        // Arrange
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, COUPON.getId());

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> orderFacade.order(criteria));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("[실패] 주문 - ISSUED 상태가 아닌 쿠폰(NOT_STATUS_ISSUED_COUPON)")
    void orderWithCoupon_issuedCouponNotStatusIssuedCoupon() {

        // Arrange
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(new IssuedCoupon(USER_ID, COUPON.getId()));
        issuedCoupon.use(); // 상태 변경 (ISSUED -> USED)

        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, COUPON.getId());

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> orderFacade.order(criteria));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_STATUS_ISSUED_COUPON);

    }

    @Test
    @DisplayName("[성공] 주문 - 재고가 없는 경우 보류 상태")
    void order_outOfStock() {

        // Arrange
        ORDER_ITEMS = List.of(
                new OrderCriteria.OrderItem(OPTION1.getId(), 30),
                new OrderCriteria.OrderItem(OPTION2.getId(), 20),
                new OrderCriteria.OrderItem(OPTION3.getId(), 110)
        );
        OrderCriteria.Order criteria = new OrderCriteria.Order(USER_ID, PRODUCT.getId(), ORDER_ITEMS, null);

        // Act
        OrderResult.Create result = orderFacade.order(criteria);

        // Assert
        Order order = orderRepository.findById(result.orderId()).get();
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getTotalAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 110);
        assertThat(order.getDiscountAmount()).isEqualTo(0L);
        assertThat(order.getPaymentAmount()).isEqualTo(1000L * 30 + 2000L * 20 + 3000L * 110);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        ProductOption productOption1 = productOptionRepository.findById(OPTION1.getId()).get();
        OrderItem orderItem1 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption1.getId()).get();
        assertThat(orderItem1.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem1.getQuantity()).isEqualTo(30);

        ProductOption productOption2 = productOptionRepository.findById(OPTION2.getId()).get();
        OrderItem orderItem2 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption2.getId()).get();
        assertThat(orderItem2.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderItem2.getQuantity()).isEqualTo(20);

        ProductOption productOption3 = productOptionRepository.findById(OPTION3.getId()).get();
        assertThat(productOption3.getStock()).isEqualTo(100);
        OrderItem orderItem3 = orderItemRepository.findByOrderIdAndProductOptionId(order.getId(), productOption3.getId()).get();
        assertThat(orderItem3.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderItem3.getQuantity()).isEqualTo(110);

        Payment payment = paymentRepository.findByOrderId(order.getId()).get();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(order.getPaymentAmount());
    }
    

}
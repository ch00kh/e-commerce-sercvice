package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] OrderService")
class OrderServiceIntegrationTest {

    @MockitoBean
    private OrderEventPublisher publisher;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderService orderService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Long ORDER_ID;

    private OrderCommand.OrderItem ORDER_ITEM1;
    private OrderCommand.OrderItem ORDER_ITEM2;
    private List<OrderCommand.OrderItem> ORDER_ITEMS;

    @BeforeEach
    void setup() {
        USER_ID = 1L;
        COUPON_ID = 11L;
        ORDER_ID = 111L;

        ORDER_ITEM1 = new OrderCommand.OrderItem(1L, 10000L, 1L);
        ORDER_ITEM2 = new OrderCommand.OrderItem(2L, 5000L, 2L);
        ORDER_ITEMS = List.of(ORDER_ITEM1, ORDER_ITEM2);
    }

    @Test
    @DisplayName("사용자ID와 주문아이템으로 주문을 생성한다.")
    void createOrder_hasNoCoupon_ok() {

        // Arrange
        OrderCommand.Create command = new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS);

        // Act
        OrderInfo.Create orderInfo = orderService.createOrder(command);

        // Assert
        Order actual = orderRepository.findById(orderInfo.orderId());
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(actual.getTotalAmount()).isEqualTo(20000L);
        assertThat(actual.getDiscountAmount()).isEqualTo(0L);
        assertThat(actual.getPaymentAmount()).isEqualTo(20000L);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(actual.getId());
        assertThat(orderItems).hasSize(2);
    }

    @Test
    @DisplayName("주문 후 재고가 부족한 경우 주문아이템의 상태가 변경(PENDING)된다.")
    void holdOrders() {

        // Arrange
        Long productOptionId = 1L;
        OrderInfo.Create orderInfo = orderService.createOrder(new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS));
        ProductInfo.OptionDetail optionDetail = new ProductInfo.OptionDetail(productOptionId, false, 2L, 1L);
        OrderCommand.handleOrders command = new OrderCommand.handleOrders(orderInfo.orderId(), List.of(optionDetail));

        // Act
        orderService.holdOrders(command);

        // Assert
        OrderItem actual = orderItemRepository.findByOrderIdAndProductOptionId(orderInfo.orderId(), productOptionId);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class useCoupon {

        @Test
        @DisplayName("쿠폰을 사용하지 않고 주문할 수 있다.")
        void useCoupon_couponIsNull() {

            // Arrange
            OrderInfo.Create orderInfo = orderService.createOrder(new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS));

            CouponInfo.CouponAggregate couponInfo = new CouponInfo.CouponAggregate(null, null, null, null, null);
            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(orderInfo.orderId(), couponInfo.couponId(), couponInfo.discountPrice());

            // Act
            OrderInfo.Create actualInfo = orderService.applyCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isNull();
        }

        @Test
        @DisplayName("쿠폰 적용 시 주문의 결제 금액이 변경된다.")
        void useCoupon_totalAmountGtDiscountAmount() {

            // Arrange
            OrderInfo.Create orderInfo = orderService.createOrder(new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS)); // 20000L
            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(orderInfo.orderId(), COUPON_ID, 3000L);

            // Act
            orderInfo = orderService.applyCoupon(command);

            // Assert
            Order actual = orderRepository.findById(orderInfo.orderId());
            assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getTotalAmount()).isEqualTo(20000L);
            assertThat(actual.getDiscountAmount()).isEqualTo(3000L);
            assertThat(actual.getPaymentAmount()).isEqualTo(17000L);
        }

        @Test
        @DisplayName("쿠폰 적용 시 주문 주문금액보다 할인금액이 큰 경우 결제금액은 0으로 변경된다.")
        void useCoupon_totalAmountLtDiscountAmount() {

            // Arrange
            OrderInfo.Create orderInfo = orderService.createOrder(new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS)); // 20000L
            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(orderInfo.orderId(), COUPON_ID, 30000L);

            // Act
            orderInfo = orderService.applyCoupon(command);

            // Assert
            Order actual = orderRepository.findById(orderInfo.orderId());
            assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getTotalAmount()).isEqualTo(20000L);
            assertThat(actual.getDiscountAmount()).isEqualTo(20000L);
            assertThat(actual.getPaymentAmount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class FindById {

        @Test
        @DisplayName("주문ID로 생성된 주문을 조회한다.")
        void findById_ok() {

            // Arrange
            OrderCommand.Create command = new OrderCommand.Create(USER_ID, COUPON_ID, ORDER_ITEMS);

            // Act
            OrderInfo.Create orderInfo = orderService.createOrder(command);

            // Assert
            Order actual = orderRepository.findById(orderInfo.orderId());

            assertThat(actual).isNotNull();
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getTotalAmount()).isEqualTo(20000L);
        }

        @Test
        @DisplayName("주문이 생성되지 않아 주문을 찾을 수 없다.")
        void findById_NotFound() {

            // Arrange
            OrderCommand.Find command = new OrderCommand.Find(999L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> orderService.findById(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("주문ID로 주문을 결제한다.")
    void pay_ok() {

        // Arrange
        Order order = orderRepository.save(new Order(USER_ID, COUPON_ID, 10000L));

        // Act
        order = orderService.pay(new OrderCommand.Find(order.getId()));

        // Assert
        Order actual = orderRepository.findById(order.getId());
        assertThat(actual).isNotNull();
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
        assertThat(actual.getTotalAmount()).isEqualTo(10000L);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);
    }

    @Test
    @DisplayName("생성된 주문이 없어 주문 결제할 수 없다.")
    void pay_NotFound() {

        // Arrange
        OrderCommand.Find command = new OrderCommand.Find(999L);

        // Act
        GlobalException exception = assertThrows(GlobalException.class, () -> orderService.pay(command));

        // Assert
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("3일간 상위 5개의 인기 판매상품 조회한다.")
    void findBestSelling_ok() {

        // Arrange
        Product PRODUCT1 = productRepository.save(new Product("양반", "김"));
        ProductOption PRODUCT1_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "들기름 김", 1000L, 1000L));
        ProductOption PRODUCT1_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "참기름 김", 1250L, 500L));

        Product PRODUCT2 = productRepository.save(new Product("엽기떡볶이", "떡볶이"));
        ProductOption PRODUCT2_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "초보 맛", 12000L, 1000L));
        ProductOption PRODUCT2_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "중간 맛", 12500L, 500L));
        ProductOption PRODUCT2_OPTION3 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "죽을 맛", 13000L, 150L));

        Order order1 = orderRepository.save(new Order(USER_ID, 0L));
        orderItemRepository.save(new OrderItem(order1.getId(), PRODUCT1_OPTION1.getId(), 1000L, 10L));
        orderItemRepository.save(new OrderItem(order1.getId(), PRODUCT1_OPTION2.getId(), 1250L, 30L));
        orderItemRepository.save(new OrderItem(order1.getId(), PRODUCT1_OPTION2.getId(), 1250L, 10L));

        Order order2 = orderRepository.save(new Order(USER_ID, 0L));
        orderItemRepository.save(new OrderItem(order2.getId(), PRODUCT1_OPTION1.getId(), 1000L, 10L));
        orderItemRepository.save(new OrderItem(order2.getId(), PRODUCT2_OPTION1.getId(), 12000L, 50L));
        orderItemRepository.save(new OrderItem(order2.getId(), PRODUCT2_OPTION2.getId(), 12500L, 40L));

        Order order3 = orderRepository.save(new Order(USER_ID, 0L));
        orderItemRepository.save(new OrderItem(order3.getId(), PRODUCT2_OPTION1.getId(), 12000L, 20L));
        orderItemRepository.save(new OrderItem(order3.getId(), PRODUCT2_OPTION2.getId(), 12500L, 30L));
        orderItemRepository.save(new OrderItem(order3.getId(), PRODUCT2_OPTION3.getId(), 13000L, 40L));
        orderItemRepository.save(new OrderItem(order3.getId(), PRODUCT1_OPTION2.getId(), 1250L, 50L));

        // Act
        List<OrderInfo.Best> result = orderService.findBestSelling(new OrderCommand.FindBest(3, 5));

        // Assert
        log.info("result: {}", result);
        assertThat(result.size()).isEqualTo(5);
    }
}
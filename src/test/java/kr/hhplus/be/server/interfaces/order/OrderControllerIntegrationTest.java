package kr.hhplus.be.server.interfaces.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.DatabaseClearExtension;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.application.user.UserFacade;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.interfaces.order.controller.OrderController;
import kr.hhplus.be.server.interfaces.order.dto.OrderRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@DisplayName("[통합테스트] OrderController")
class OrderControllerIntegrationTest {

    @Autowired
    private OrderController orderController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    private UserResult.Create USER_RESULT;
    private Coupon COUPON;
    private CouponResult.Issued COUPON_RESULT;

    private Product PRODUCT;
    private ProductOption PRODUCT_OPTION1;
    private ProductOption PRODUCT_OPTION2;

    @BeforeEach
    void setUp() {
        USER_RESULT = userFacade.createUser(new UserCriteria.Create("추경현"));

        COUPON = couponRepository.save(new Coupon(5000L, 100L));
        COUPON_RESULT = couponFacade.firstComeFirstIssue(new CouponCriteria.Issue(USER_RESULT.id(), COUPON.getId()));

        PRODUCT = productRepository.save(new Product("맥도날드", "햄버거"));
        PRODUCT_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "빅맥", 1000L, 100L));
        PRODUCT_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "맥스파이시", 1000L, 100L));
    }

    @Test
    @DisplayName("[성공] 주문 생성 (쿠폰 사용X)")
    void order() throws Exception {

        // Arrange
        List<OrderRequest.Item> items = List.of(
                new OrderRequest.Item(PRODUCT_OPTION1.getId(), 10L),
                new OrderRequest.Item(PRODUCT_OPTION2.getId(), 5L)
        );

        // Act
        OrderRequest.Create request = new OrderRequest.Create(USER_RESULT.id(), PRODUCT.getId(), items, null);
        ResponseEntity<OrderResponse.Create> response = orderController.order(request);

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
        {
            "orderId": 1,
            "userId": 1,
            "status": "CREATED",
            "totalAmount": 15000,
            "discountAmount": 0,
            "paymentAmount": 15000
        }
        """;
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
    }

    @Test
    @DisplayName("[성공] 주문 생성 (쿠폰 사용O)")
    void orderWithCoupon() throws Exception {

        // Arrange
        List<OrderRequest.Item> items = List.of(
                new OrderRequest.Item(PRODUCT_OPTION1.getId(), 10L),
                new OrderRequest.Item(PRODUCT_OPTION2.getId(), 5L)
        );

        // Act
        OrderRequest.Create request = new OrderRequest.Create(USER_RESULT.id(), PRODUCT.getId(), items, COUPON_RESULT.couponId());
        ResponseEntity<OrderResponse.Create> response = orderController.order(request);

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
        {
            "orderId": 1,
            "userId": 1,
            "status": "CREATED",
            "totalAmount": 15000,
            "discountAmount": 5000,
            "paymentAmount": 10000
        }
        """;
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(USER_RESULT.id(), COUPON_RESULT.couponId());
        assertThat(issuedCoupon.getStatus()).isEqualTo(CouponStatus.USED);
    }
}
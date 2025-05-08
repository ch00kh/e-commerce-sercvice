package kr.hhplus.be.server.interfaces.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.application.user.UserFacade;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.interfaces.payment.controller.PaymentController;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] PaymentController")
class PaymentControllerIntegrationTest {

    @Autowired
    PaymentController paymentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private UserResult.Create USER_RESULT;
    private OrderResult.Create ORDER_RESULT;

    private Product PRODUCT;
    private ProductOption PRODUCT_OPTION1;
    private ProductOption PRODUCT_OPTION2;



    @BeforeEach
    void setUp() {
        USER_RESULT = userFacade.createUser(new UserCriteria.Create("추경현"));
        balanceFacade.charge(new BalanceCriteria.Charge(USER_RESULT.id(), 20000L));

        PRODUCT = productRepository.save(new Product("맥도날드", "햄버거"));
        PRODUCT_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "빅맥", 1000L, 100L));
        PRODUCT_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "맥스파이시", 1000L, 100L));

        List<OrderCriteria.OrderItem> items = List.of(
                new OrderCriteria.OrderItem(PRODUCT_OPTION1.getId(), 10L),
                new OrderCriteria.OrderItem(PRODUCT_OPTION2.getId(), 5L)
        );

        ORDER_RESULT = orderFacade.order(new OrderCriteria.Create(USER_RESULT.id(), PRODUCT.getId(), items, null));
    }


    @Test
    @DisplayName("주문ID와 전체 결제금액을 입력받아 전체 결제금액에 대한 결제를 처리한다.")
    void payAllAmount() throws Exception {

        // Arrange
        PaymentRequest request = new PaymentRequest(ORDER_RESULT.orderId(), 15000L);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.pay(request);

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
        {
            "orderId": 1,
            "orderStatus": "PAYED",
            "paymentId": 1,
            "paymentStatus": "PAYED"
        }
        """;
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
    }

    @Test
    @DisplayName("주문ID와 일부 결제금액을 입력받아 일부 결제금액에 대한 결제를 처리한다.")
    void payAnyAmount() throws Exception {

        // Arrange
        PaymentRequest request = new PaymentRequest(ORDER_RESULT.orderId(), 10000L);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.pay(request);

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
        {
            "orderId": 1,
            "orderStatus": "PAYED",
            "paymentId": 1,
            "paymentStatus": "PENDING"
        }
        """;
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
    }
}
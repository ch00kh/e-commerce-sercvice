package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.DatabaseClearExtension;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] PaymentService")
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private Payment PAYMENT;
    private Long ORDER_ID;

    @BeforeEach
    void setUp() {
        ORDER_ID = 100L;
        PAYMENT = paymentRepository.save(new Payment(ORDER_ID, 100000L));
    }

    @Test
    @DisplayName("주문ID로 결제를 조회한다.")
    void findPayment_ok() {

        // Arrange
        PaymentCommand.FindOrder command = new PaymentCommand.FindOrder(ORDER_ID);

        // Act
        Payment payment = paymentService.findPayment(command);

        // Assert
        Payment actual = paymentRepository.findById(payment.getId());
        assertThat(actual.getOrderId()).isEqualTo(100L);
        assertThat(actual.getAmount()).isEqualTo(100000L);
        assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(actual.getPaidAt()).isNull();
    }

    @Nested
    @DisplayName("결제")
    class pay {

        @Test
        @DisplayName("전체 결제금액에 대한 결제를 한다.")
        void payAllAmount_ok() {

            // Arrange
            PaymentCommand.Pay command = new PaymentCommand.Pay(PAYMENT.getId(), 100000L);

            // Act
            Payment payment = paymentService.pay(command);

            // Assert
            Payment actual = paymentRepository.findById(payment.getId());

            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(actual.getAmount()).isEqualTo(0L);
            assertThat(actual.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("일부 금액에 대한 결제를 한다. 잔여 결제금액 있어 결제 상태는 유지된다.")
        void paySomeAmount_ok() {

            // Arrange
            PaymentCommand.Pay command = new PaymentCommand.Pay(PAYMENT.getId(), 50000L);

            // Act
            Payment payment = paymentService.pay(command);

            // Assert
            Payment actual = paymentRepository.findById(payment.getId());
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(actual.getAmount()).isEqualTo(50000L);
            assertThat(actual.getPaidAt()).isNotNull();
        }

    }

}
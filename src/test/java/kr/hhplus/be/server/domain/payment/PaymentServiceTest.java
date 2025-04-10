package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] PaymentServiceTest")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Long USER_ID;
    private Long PAYMENT_ID;
    private Long ORDER_ID;
    private Payment PAYMENT;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        PAYMENT_ID = 1L;
        ORDER_ID = 100L;

        PAYMENT = Payment.builder()
                .id(PAYMENT_ID)
                .orderId(ORDER_ID)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("결제 조회")
    class findPayment {

        @Test
        @DisplayName("[성공] 결제 조회")
        void findPayment_ok() {

            // Arrange
            Payment payedPayment = PAYMENT.pay(1000L);

            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(payedPayment));

            // Act
            Payment actual = paymentService.findPayment(new PaymentCommand.Find(PAYMENT_ID));

            // Assert
            verify(paymentRepository, times(1)).findById(PAYMENT_ID);
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isEqualTo(PAYMENT_ID);
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PAYED);
        }

        @Test
        @DisplayName("[실패] 결제 조회 -> 결제 없음(NOT_FOUND)")
        void findPayment_NotFound() {

            // Arrange
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> paymentService.findPayment(new PaymentCommand.Find(PAYMENT_ID)));

            // Assert
            verify(paymentRepository, times(1)).findById(PAYMENT_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

    }

    @Nested
    @DisplayName("결제")
    class pay {

        @Test
        @DisplayName("[성공] 결제")
        void pay_ok() {

            // Arrange
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(PAYMENT));

            // Act
            Payment result = paymentService.pay(new PaymentCommand.Pay(PAYMENT_ID, 10000L));

            // Assert
            verify(paymentRepository, times(1)).findById(PAYMENT_ID);
            assertThat(result.getId()).isEqualTo(PAYMENT_ID);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(result.getAmount()).isEqualTo(10000L);
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("[실패] 결제 -> 결제 없음(NOT_FOUND)")
        void pay_NotFound() {

            // Arrange
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> paymentService.pay(new PaymentCommand.Pay(PAYMENT_ID, 10000L)));

            // Assert
            verify(paymentRepository).findById(PAYMENT_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

}
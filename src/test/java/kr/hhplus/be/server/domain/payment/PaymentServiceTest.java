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

    private Payment PAYMENT;

    @BeforeEach
    void setUp() {
        PAYMENT = new Payment(100L, 100000L);
    }

    @Nested
    @DisplayName("결제 조회")
    class findPayment {

        @Test
        @DisplayName("주문ID로 결제를 조회한다.")
        void findPayment_ok() {

            // Arrange
            when(paymentRepository.findByOrderId(anyLong())).thenReturn(PAYMENT);

            // Act
            Payment actual = paymentService.findPayment(new PaymentCommand.FindOrder(anyLong()));

            // Assert
            verify(paymentRepository, times(1)).findByOrderId(anyLong());
            assertThat(actual.getOrderId()).isEqualTo(100L);
            assertThat(actual.getAmount()).isEqualTo(100000L);
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(actual.getPaidAt()).isNull();
        }

        @Test
        @DisplayName("주문ID로 결제를 찾을 수 없어 결제 조회를 할 수 없다.")
        void findPayment_NotFound() {

            // Arrange
            when(paymentRepository.findByOrderId(anyLong())).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> paymentService.findPayment(new PaymentCommand.FindOrder(anyLong())));

            // Assert
            verify(paymentRepository, times(1)).findByOrderId(anyLong());
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("결제")
    class pay {

        @Test
        @DisplayName("전체 결제금액에 대한 결제를 한다.")
        void payAllAmount_ok() {

            // Arrange
            when(paymentRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(PAYMENT);

            // Act
            Payment result = paymentService.pay(new PaymentCommand.Pay(anyLong(), 100000L));

            // Assert
            verify(paymentRepository, times(1)).findByIdWithOptimisticLock(anyLong());
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(result.getAmount()).isEqualTo(0L);
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("일부 금액에 대한 결제를 한다. 잔여 결제금액 있어 결제 상태는 유지된다.")
        void paySomeAmount_ok() {

            // Arrange
            when(paymentRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(PAYMENT);

            // Act
            Payment result = paymentService.pay(new PaymentCommand.Pay(anyLong(), 50000L));

            // Assert
            verify(paymentRepository, times(1)).findByIdWithOptimisticLock((anyLong()));
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(result.getAmount()).isEqualTo(50000L);
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("생성된 결제가 없어 결제를 찾을 수 없다.")
        void pay_NotFound() {

            // Arrange
            when(paymentRepository.findByIdWithOptimisticLock(anyLong())).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> paymentService.pay(new PaymentCommand.Pay(anyLong(), 10000L)));

            // Assert
            verify(paymentRepository).findByIdWithOptimisticLock(anyLong());
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

}
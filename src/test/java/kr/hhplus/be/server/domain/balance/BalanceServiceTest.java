package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.entity.TransactionType;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] BalanceService")
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private BalanceHistoryRepository balanceHistoryRepository;

    @InjectMocks
    private BalanceService balanceService;

    Long USER_ID;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
    }

    @Nested
    @DisplayName("잔액 조회")
    class findBalance{

        @Test
        @DisplayName("사용자 ID로 잔액을 조회한다.")
        void findBalance_ok() {

            // Arrange
            Balance balance = new Balance(USER_ID, 1000L);
            BalanceCommand.Find command = new BalanceCommand.Find(USER_ID);

            when(balanceRepository.findByUserId(USER_ID)).thenReturn(balance);

            // Act
            Balance actualBalance = balanceService.find(command);

            // Assert
            verify(balanceRepository, times(1)).findByUserId(USER_ID);

            assertThat(actualBalance.getUserId()).isEqualTo(1L);
            assertThat(actualBalance.getBalance()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("사용자를 찾을 수 없어 잔액 조회를 할 수 없다.")
        void findBalance_notFound() {

            // Arrange
            BalanceCommand.Find command = new BalanceCommand.Find(USER_ID);

            when(balanceRepository.findByUserId(USER_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.find(command));

            // Assert
            verify(balanceRepository, times(1)).findByUserId(USER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("잔액 충전")
    class charge {

        @Test
        @DisplayName("사용자ID와 충전금액을 받아 잔액을 충전한다.")
        void charge_ok() {

            // Arrange
            Balance balance = new Balance(USER_ID, 1000L);
            when(balanceRepository.findByUserIdWithOptimisticLock(USER_ID)).thenReturn(balance);

            // Act
            Balance actualBalance = balanceService.charge(new BalanceCommand.Charge(USER_ID, 1000L));

            // Assert
            verify(balanceRepository, times(1)).findByUserIdWithOptimisticLock(USER_ID);

            assertThat(actualBalance.getUserId()).isEqualTo(1L);
            assertThat(actualBalance.getBalance()).isEqualTo(2000L); // 1000+1000

            ArgumentCaptor<BalanceHistory> captor = ArgumentCaptor.forClass(BalanceHistory.class);
            verify(balanceHistoryRepository, times(1)).save(captor.capture());

            BalanceHistory balanceHistory = captor.getValue();
            assertThat(balanceHistory.getIssuedCouponId()).isEqualTo(null);
            assertThat(balanceHistory.getAmount()).isEqualTo(1000L);
            assertThat(balanceHistory.getTransactionType()).isEqualTo(TransactionType.CHARGE);
        }

        @Test
        @DisplayName("잔고가 없거나 사용자를 찾을 수 없어 잔액 충전을 할 수 없다.")
        void charge_notFound() {

            // Arrange
            when(balanceRepository.findByUserIdWithOptimisticLock(USER_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.charge(new BalanceCommand.Charge(1L, 1000L)));

            // Assert
            verify(balanceRepository, times(1)).findByUserIdWithOptimisticLock(USER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("충전 금액은 음수일 수 없어 잔액 충전을 할 수 없다.")
        void charge_invalidChargeAmount() {

            // Arrange
            Balance balance = new Balance(USER_ID, 1000L);
            when(balanceRepository.findByUserIdWithOptimisticLock(USER_ID)).thenReturn(balance);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.charge(new BalanceCommand.Charge(1L, -1000L)));

            // Assert
            verify(balanceRepository, times(1)).findByUserIdWithOptimisticLock(USER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);
        }
    }

    @Nested
    @DisplayName("잔고 차감")
    class reduce {

        @Test
        @DisplayName("잔액이 여유가 있는 경우 잔액을 차감한다.")
        void reduce_ok() {

            // Arrange
            Balance balance = new Balance(USER_ID, 1000L);
            when(balanceRepository.findByUserIdWithPessimisticLock(USER_ID)).thenReturn(balance);

            // Act
            Balance actual = balanceService.reduce(new BalanceCommand.Reduce(USER_ID, 500L, null));

            // Assert
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getBalance()).isEqualTo(500L);

            verify(balanceRepository).findByUserIdWithPessimisticLock(USER_ID);
        }

        @Test
        @DisplayName("잔액이 부족한 경우 잔액을 차감할 수 없다.")
        void reduce_insufficientBalance() {

            // Arrange
            Balance balance = new Balance(USER_ID, 1000L);
            when(balanceRepository.findByUserIdWithPessimisticLock(USER_ID)).thenReturn(balance);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () ->  balanceService.reduce(new BalanceCommand.Reduce(USER_ID, 1500L, null)));

            // Assert
            verify(balanceRepository).findByUserIdWithPessimisticLock(USER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
        }

        @Test
        @DisplayName("잔고를 찾을 수 없어 잔액 차감을 할 수 없다.")
        void reduceBalance_notFound() {

            // Arrange
            when(balanceRepository.findByUserIdWithPessimisticLock(USER_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () ->  balanceService.reduce(new BalanceCommand.Reduce(USER_ID, 500L, null)));

            // Assert
            verify(balanceRepository).findByUserIdWithPessimisticLock(USER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}
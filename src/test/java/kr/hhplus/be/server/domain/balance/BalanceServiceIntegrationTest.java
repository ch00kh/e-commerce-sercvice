package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[통합테스트] BalanceService")
@Transactional
class BalanceServiceIntegrationTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceHistoryRepository balanceHistoryRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    User USER;
    Balance BALANCE;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        balanceRepository.deleteAll();

        USER = userRepository.save(new User("추경현"));
        BALANCE = balanceRepository.save(new Balance(USER.getId(), 1000L));

        log.info("USER ID: {}", USER.getId());
        log.info("BALANCE ID: {}", BALANCE.getId());
    }

    @Nested
    @DisplayName("잔액 조회")
    class findBalance{

        @Test
        @DisplayName("[성공] 잔액 조회")
        void findBalance_ok() {

            // Arrange
            BalanceCommand.Find command = new BalanceCommand.Find(USER.getId());

            // Act
            Balance balance = balanceService.find(command);

            // Assert
            assertThat(balance.getBalance()).isEqualTo(1000L);

            Balance actual = balanceRepository.findByUserId(USER.getId());
            assertThat(actual.getBalance()).isEqualTo(1000L);

        }

        @Test
        @DisplayName("[실패] 잔액 조회 - 사용자 없음 예외(NOT_FOUND)")
        void findBalance_notFound() {

            // Arrange
            BalanceCommand.Find command = new BalanceCommand.Find(999L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.find(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("잔액 충전")
    class charge {

        @Test
        @DisplayName("[성공] 잔액 충전")
        void charge_ok() {

            // Arrange
            BalanceCommand.Charge command = new BalanceCommand.Charge(USER.getId(), 1000L);

            // Act
            Balance balance = balanceService.charge(command);

            // Assert
            assertThat(balance.getBalance()).isEqualTo(2000L); // 1000+1000

            Balance actualBalance = balanceRepository.findByUserId(USER.getId());
            assertThat(actualBalance.getBalance()).isEqualTo(2000L);

            List<BalanceHistory> actualBalanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
            assertThat(actualBalanceHistory).hasSize(1);
        }

        @Test
        @DisplayName("[실패] 잔액 충전 - 없는 잔고(존재하지 않는 사용자) 예외(NOT_FOUND)")
        void charge_notFound() {

            // Arrange
            BalanceCommand.Charge command = new BalanceCommand.Charge(9999L, 1000L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.charge(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("[실패] 잔액 충전 - 유효하지 않은 충전 금액 예외 (INVALID_CHARGE_AMOUNT)")
        void charge_invalidChargeAmount() {

            // Arrange
            BalanceCommand.Charge command = new BalanceCommand.Charge(USER.getId(), -1000L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.charge(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);
        }
    }

    @Nested
    @DisplayName("잔고 차감")
    class reduce {

        @Test
        @DisplayName("[성공] 잔고 여유")
        void reduce_ok() {

            // Arrange
            BalanceCommand.Reduce command = new BalanceCommand.Reduce(USER.getId(), 1000L, null);

            // Act
            Balance balance = balanceService.reduce(command);

            // Assert
            assertThat(balance.getBalance()).isEqualTo(0);

            Balance actualBalance = balanceRepository.findByUserId(USER.getId());
            assertThat(actualBalance.getBalance()).isEqualTo(0);

            List<BalanceHistory> actualBalanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
            assertThat(actualBalanceHistory).hasSize(1);
        }

        @Test
        @DisplayName("[실패] 잔고 부족 (잔고금액 < 결제금액) -> (INSUFFICIENT_BALANCE)")
        void reduce_insufficientBalance() {

            // Arrange
            BalanceCommand.Reduce command = new BalanceCommand.Reduce(USER.getId(), 2000L, null);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,() ->  balanceService.reduce(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
        }

        @Test
        @DisplayName("[실패] 잔액 차감 -> 사용자 없음(NOT_FOUND)")
        void reduceBalance_notFound() {

            // Arrange
            BalanceCommand.Reduce command = new BalanceCommand.Reduce(999L, 1000L, null);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,() ->  balanceService.reduce(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}
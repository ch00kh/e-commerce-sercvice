package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] BalanceService")
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
        USER = userRepository.save(new User("추경현"));
        BALANCE = balanceRepository.save(new Balance(USER.getId(), 1000L));
    }

    @Nested
    @DisplayName("잔액 조회")
    class findBalance{

        @Test
        @DisplayName("사용자 ID로 잔액을 조회한다.")
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
        @DisplayName("사용자를 찾을 수 없어 잔액 조회를 할 수 없다.")
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
        @DisplayName("사용자ID와 충전금액을 받아 잔액을 충전한다.")
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
        @DisplayName("잔고가 없거나 사용자를 찾을 수 없어 잔액 충전을 할 수 없다.")
        void charge_notFound() {

            // Arrange
            BalanceCommand.Charge command = new BalanceCommand.Charge(9999L, 1000L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> balanceService.charge(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("충전 금액은 음수일 수 없어 잔액 충전을 할 수 없다.")
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
    @DisplayName("잔액 차감")
    class reduce {

        @Test
        @DisplayName("잔액이 여유가 있는 경우 잔액을 차감한다.")
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
        @DisplayName("잔액이 부족한 경우 잔액을 차감할 수 없다.")
        void reduce_insufficientBalance() {

            // Arrange
            BalanceCommand.Reduce command = new BalanceCommand.Reduce(USER.getId(), 2000L, null);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,() ->  balanceService.reduce(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
        }

        @Test
        @DisplayName("잔고를 찾을 수 없어 잔액 차감을 할 수 없다.")
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
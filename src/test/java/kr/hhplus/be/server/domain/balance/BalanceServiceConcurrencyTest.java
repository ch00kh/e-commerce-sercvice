package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] BalanceService")
class BalanceServiceConcurrencyTest {

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
        balanceHistoryRepository.deleteAll();

        USER = userRepository.save(new User("추경현"));
        BALANCE = balanceRepository.save(new Balance(USER.getId(), 0L));

        log.info("USER ID: {}", USER.getId());
        log.info("BALANCE ID: {}", BALANCE.getId());
    }

    @Test
    @DisplayName("잔액 충전")
    void charge_ok() throws InterruptedException {

        // Arrange
        int threadCount = 20;
        int threadPool = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    BalanceCommand.Charge command = new BalanceCommand.Charge(USER.getId(), 1000L);
                    balanceService.charge(command);

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error charging balance: {}", e.getMessage());

                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());

        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        Balance actualBalance = balanceRepository.findByUserId(USER.getId());
        assertThat(actualBalance.getBalance()).isEqualTo(1000L * threadCount);

        List<BalanceHistory> actualBalanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
        assertThat(actualBalanceHistory).hasSize(threadCount);
    }

    @Test
    @DisplayName("잔고 차감")
    void reduce_ok() throws InterruptedException {

        // Arrange
        int threadCount = 20;
        int threadPool = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        balanceService.charge(new BalanceCommand.Charge(USER.getId(), 1000L));

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    BalanceCommand.Reduce command = new BalanceCommand.Reduce(USER.getId(), 10L, null);
                    balanceService.reduce(command);

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error reducing balance: {}", e.getMessage());

                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());

        Balance actualBalance = balanceRepository.findByUserId(USER.getId());
        assertThat(actualBalance.getBalance()).isEqualTo(1000L - threadCount * 10L);

        List<BalanceHistory> actualBalanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
        assertThat(actualBalanceHistory).hasSize(threadCount + 1); // 충전 1번 + 차감 스레드 수
    }
}
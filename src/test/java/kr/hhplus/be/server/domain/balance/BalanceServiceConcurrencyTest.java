package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import kr.hhplus.be.server.surpport.cleaner.KafkaClearExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@ExtendWith(DatabaseClearExtension.class)
@ExtendWith(KafkaClearExtension.class)
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
        USER = userRepository.save(new User("추경현"));
        BALANCE = balanceRepository.save(new Balance(USER.getId(), 10000L));
    }

    @Test
    @DisplayName("사용자 잔액 충전 시 부분적으로 성공한다. 재시도가 없어 일부 요청은 실패한다.")
    void charge_ok() throws InterruptedException {

        // Arrange
        int threadCount = 10;
        int threadPool = 10;
        long chargeAmount = 1000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    BalanceCommand.Charge command = new BalanceCommand.Charge(USER.getId(), chargeAmount);
                    balanceService.charge(command);

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error charging balance: {}", e.getMessage());

                } finally {
                    taskLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        taskLatch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        Balance balance = balanceRepository.findByUserId(USER.getId());
        assertThat(balance.getBalance()).isEqualTo(10000L + (successCount.get() * chargeAmount));

        List<BalanceHistory> balanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
        assertThat(balanceHistory).hasSize(successCount.get());
    }

    @Test
    @DisplayName("결제 중 잔액 차감 시 잔고가 여유가 있는 경우 모든 요청 성공한다.")
    void reduce_ok() throws InterruptedException {

        // Arrange
        int threadCount = 10;
        int threadPool = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    balanceService.reduce(new BalanceCommand.Reduce(USER.getId(), 100L, null));

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error reducing balance: {}", e.getMessage());

                } finally {
                    taskLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        taskLatch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        Balance balance = balanceRepository.findByUserId(USER.getId());
        assertThat(balance.getBalance()).isEqualTo(10000L - (threadCount * 100L));

        List<BalanceHistory> balanceHistory = balanceHistoryRepository.findByBalanceId(BALANCE.getId());
        assertThat(balanceHistory).hasSize(threadCount);
    }
}
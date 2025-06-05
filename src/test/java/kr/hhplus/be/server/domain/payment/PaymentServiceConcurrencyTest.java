package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] PaymentService")
class PaymentServiceConcurrencyTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private Payment PAYMENT;

    @BeforeEach
    void setUp() {
        PAYMENT = paymentRepository.save(new Payment(1L, 10000L));
    }

    @Test
    @DisplayName("결제시 부분적으로 성공한다. 재시도가 없어 일부 요청은 실패한다.")
    void payAllAmount_ok() throws InterruptedException {

        // Arrange
        int threadCount = 10;
        int threadPool = 3;

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

                    paymentService.pay(new PaymentCommand.Pay(PAYMENT.getId(), 1000L));
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error pay: {}", e.getMessage());
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

        Payment payment = paymentRepository.findById(PAYMENT.getId());
        assertThat(payment.getAmount()).isEqualTo(10000L - (successCount.get() * 1000L));
    }

}
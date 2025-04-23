package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.DatabaseClearExtension;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
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
    private Long ORDER_ID;

    @BeforeEach
    void setUp() {
        ORDER_ID = 100L;
        PAYMENT = paymentRepository.save(new Payment(ORDER_ID, 100000L));
    }

    @Test
    @DisplayName("결제")
    void payAllAmount_ok() throws InterruptedException {

        // Arrange
        int threadCount = 10;
        int threadPool = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    PaymentCommand.Pay command = new PaymentCommand.Pay(PAYMENT.getId(), 10000L);
                    paymentService.pay(command);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error pay: {}", e.getMessage());
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

        Payment actual = paymentRepository.findById(PAYMENT.getId());
        assertThat(actual.getAmount()).isEqualTo(0L);
        assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PAYED);
        assertThat(actual.getPaidAt()).isNotNull();
    }

}
package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.surpport.cleaner.KafkaClearExtension;
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
@ExtendWith(KafkaClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] CouponService")
class CouponServiceConcurrencyTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponService couponService;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        COUPON = couponRepository.save(new Coupon(1000L, 10L));
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 시 부분적으로 성공,실패한다. 쿠폰이 부족한 경우 쿠폰발급에 실패한다.")
    void issue_ok() throws InterruptedException {

        // Arrange
        int threadCount = 100;
        int threadPool = 8;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            long finalI = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    couponService.issue(new CouponCommand.Apply(finalI, COUPON.getId()));
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error coupon issue: {}", e.getMessage());
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

        Coupon coupon = couponRepository.findById(COUPON.getId());
        assertThat(coupon.getQuantity()).isEqualTo(0);

    }
}
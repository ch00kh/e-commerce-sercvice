package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] CouponService")
class CouponServiceConcurrencyTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponService couponService;

    private Long COUPON_ID;
    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
        COUPON_ID = COUPON.getId();
    }

    @Test
    @DisplayName("쿠폰 발급")
    void issue_ok() throws InterruptedException {

        // Arrange
        int threadCount = 100;
        int threadPool = 8;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Assert
        for (int i = 0; i < threadCount; i++) {
            long finalI = i;
            executorService.submit(() -> {
                try {

                    couponService.issue(new CouponCommand.Issue(finalI, COUPON_ID));
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error coupon issue: {}", e.getMessage());
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

        Coupon coupon = couponRepository.findById(COUPON_ID);
        assertThat(coupon.getQuantity()).isEqualTo(0);

    }
}
package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.surpport.database.RedisClearExtension;
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
@ExtendWith(RedisClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] CouponFacadeTest")
class CouponFacadeTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private CouponFacade couponFacade;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
    }

    @Test
    @DisplayName("동시에 여러 요청에 의한 선착순 쿠폰 발급은 쿠폰 발급 대기열에 들어가며, 발급수량 만큼 대기열의 발급쿠폰이 저장된다.")
    void firstComeFirstIssue() throws InterruptedException {
        // Arrange
        int threadCount = 200;
        int threadPool = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act1 - 대기열
        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    couponFacade.firstComeFirstIssue(new CouponCriteria.Enqueue(userId, COUPON.getId()));
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

        // Act2 - 발급
        couponFacade.processCouponQueue();

        // Assert
        int issuedCount = 0;
        for (int i = 0; i < threadCount; i++) {
            if (issuedCouponRepository.existsByUserIdAndCouponId((long) i, COUPON.getId())) {
                issuedCount++;
            }
        }
        log.info("Issued count: {}", issuedCount);

        assertThat((long) issuedCount).isEqualTo(COUPON.getQuantity());

        Coupon issueAfterCoupon = couponRepository.findById(COUPON.getId());
        assertThat(issueAfterCoupon.getQuantity()).isEqualTo(0);
    }
}

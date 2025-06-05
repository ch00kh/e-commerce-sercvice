package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] CouponService")
class CouponServiceIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderRepository orderRepository;

    private Long USER_ID;
    private Long COUPON_ID;
    private Coupon COUPON;
    private IssuedCoupon ISSUED_COUPON;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
        COUPON_ID = COUPON.getId();
        ISSUED_COUPON = issuedCouponRepository.save(new IssuedCoupon(USER_ID, COUPON_ID));
    }

    @Test
    @DisplayName("쿠폰 적용시 발급된 쿠폰의 상태가 변경된다.")
    void useCoupon_ok() {

        // Arrange
        CouponCommand.Use command = new CouponCommand.Use(USER_ID, COUPON_ID, 1L);
        orderRepository.save(new Order(1L, ISSUED_COUPON.getId(), 1000L));

        // Act
        couponService.use(command);

        // Assert
        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID);

        assertThat(actual.getStatus()).isEqualTo(CouponStatus.USED);
        assertThat(actual.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("쿠폰 발급에 성공한다.")
    void issue_ok() {

        // Arrange
        Coupon newCoupon = couponRepository.save(new Coupon(1000L, 100L));

        // Act
        couponService.issue(new CouponCommand.Apply(USER_ID, newCoupon.getId()));

        // Assert
        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, newCoupon.getId());
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
        assertThat(actual.getExpiredAt()).isEqualTo(LocalDate.now().plusDays(30).atStartOfDay());
    }

    @Test
    @DisplayName("유효기간이 끝난 쿠폰은 만료처리 된다.")
    void expireCoupon() throws InterruptedException {

        // Act
        couponService.changeExpiredAt(new CouponCommand.ChangeExpiredAt(USER_ID, COUPON_ID, LocalDateTime.now()));

        Thread.sleep(1000 * 3);

        couponService.expireCoupon();

        // Assert
        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID);
        assertThat(actual.getStatus()).isEqualTo(CouponStatus.EXPIRED);
    }
}
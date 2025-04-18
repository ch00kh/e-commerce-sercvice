package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[통합테스트] CouponService")
class CouponServiceIntegrationTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private CouponService couponService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Coupon COUPON;
    private IssuedCoupon ISSUED_COUPON;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
        issuedCouponRepository.deleteAll();

        USER_ID = 1L;
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
        COUPON_ID = COUPON.getId();
        ISSUED_COUPON = issuedCouponRepository.save(new IssuedCoupon(USER_ID, COUPON_ID));
    }

    @Test
    @DisplayName("[성공] 쿠폰 적용시 상태 변경 (ISSUED -> USED)")
    void useCoupon_ok() {

        // Arrange
        CouponCommand.Use command = new CouponCommand.Use(USER_ID, COUPON_ID);

        // Act
        CouponInfo.CouponAggregate couponInfo = couponService.use(command);

        // Assert
        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID).get();

        assertThat(actual.getStatus()).isEqualTo(CouponStatus.USED);
        assertThat(actual.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("[성공] 쿠폰 발급")
    void issue_ok() {

        // Arrange
        Coupon newCoupon = couponRepository.save(new Coupon(1000L, 100L));

        // Act
        IssuedCoupon issuedCoupon = couponService.issue(new CouponCommand.Issue(USER_ID, newCoupon.getId()));

        // Assert
        Coupon coupon = couponRepository.findById(issuedCoupon.getCouponId()).get();
        assertThat(coupon.getQuantity()).isEqualTo(99L);

        IssuedCoupon actual = issuedCouponRepository.findByUserIdAndCouponId(USER_ID, issuedCoupon.getCouponId()).get();
        assertThat(actual.getCouponId()).isEqualTo(issuedCoupon.getCouponId());
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
    }
}
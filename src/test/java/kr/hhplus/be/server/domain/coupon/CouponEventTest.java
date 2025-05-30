package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] CouponEvent")
class CouponEventTest {

    @Autowired
    private CouponEventPublisher publisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    private User USER;
    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        USER = userRepository.save(new User("홍길동"));
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
    }

    @Test
    @DisplayName("쿠폰 발행을 하게 되면 쿠폰 발급을 처리한다.")
    void couponApplyPublishAndConsumeTet() throws InterruptedException {

        // Arrage
        CouponEvent.Apply event = new CouponEvent.Apply(COUPON.getId(), USER.getId());

        // Act
        publisher.publish(event);

        Thread.sleep(3 * 1000);

        // then
        IssuedCoupon actualIssuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(USER.getId(), COUPON.getId());
        assertThat(actualIssuedCoupon).isNotNull();

        Coupon actualCoupon = couponRepository.findById(COUPON.getId());
        assertThat(actualCoupon.getQuantity()).isEqualTo(99L);
    }
}

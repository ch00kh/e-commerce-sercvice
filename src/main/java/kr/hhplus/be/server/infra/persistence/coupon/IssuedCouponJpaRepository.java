package kr.hhplus.be.server.infra.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

}

package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssuedCouponRepository {

    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
}

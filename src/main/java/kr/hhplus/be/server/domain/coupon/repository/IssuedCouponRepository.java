package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;

import java.util.Optional;

public interface IssuedCouponRepository {

    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    IssuedCoupon save(IssuedCoupon issuedCoupon);
}

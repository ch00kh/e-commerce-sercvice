package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;

import java.util.List;

public interface IssuedCouponRepository {

    IssuedCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    IssuedCoupon save(IssuedCoupon issuedCoupon);

    List<IssuedCoupon> findExpiredCoupons();

    IssuedCoupon findById(Long issuedCouponId);
}

package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;

public record CouponResponse(
    Long couponId
) {
    public static CouponResponse from(CouponResult.Enqueue result) {
        return new CouponResponse(result.couponId());
    }

    public static CouponResponse from(Coupon result) {
        return new CouponResponse(result.getId());
    }
}

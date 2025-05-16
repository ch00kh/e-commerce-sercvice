package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponResult;

public record CouponResponse(
    Long couponId
) {
    public static CouponResponse from(CouponResult.Enqueue result) {
        return new CouponResponse(result.couponId());
    }
}

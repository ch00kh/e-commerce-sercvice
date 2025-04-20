package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponResult;

public record CouponResponse(
    Long couponId,
    String status
) {
    public static CouponResponse from(CouponResult.Issued result) {
        return new CouponResponse(result.couponId(), result.status().name());
    }
}

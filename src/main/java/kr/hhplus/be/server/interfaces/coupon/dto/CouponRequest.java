package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;

public record CouponRequest() {

    public record Enqueue(
            Long userId,
            Long couponId
    ) {
        public CouponCriteria.Enqueue toCriteria() {
            return new CouponCriteria.Enqueue(userId, couponId);
        }
    }
}

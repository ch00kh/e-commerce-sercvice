package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;

public record CouponRequest() {

    public record Issue(
            Long userId,
            Long couponId
    ) {
        public CouponCriteria.Issue toCriteria() {
            return new CouponCriteria.Issue(userId, couponId);
        }
    }
}

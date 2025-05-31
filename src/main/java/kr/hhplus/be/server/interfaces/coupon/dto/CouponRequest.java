package kr.hhplus.be.server.interfaces.coupon.dto;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;

public record CouponRequest() {

    public record Apply(
            Long userId,
            Long couponId
    ) {
        public CouponCriteria.Enqueue toCriteria() {
            return new CouponCriteria.Enqueue(userId, couponId);
        }

        public CouponCommand.Apply toCommand() {
            return new CouponCommand.Apply(userId, couponId);
        }
    }
}

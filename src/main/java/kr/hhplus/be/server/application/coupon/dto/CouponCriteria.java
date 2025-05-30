package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;

public record CouponCriteria() {

    public record Enqueue(
            Long userId,
            Long couponId
    ) {
        public CouponCommand.Apply toCommand() {
            return new CouponCommand.Apply(userId, couponId);
        }
    }

}

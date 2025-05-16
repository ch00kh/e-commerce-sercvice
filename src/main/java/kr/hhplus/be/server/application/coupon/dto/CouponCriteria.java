package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;

public record CouponCriteria() {


    public record Enqueue(
            Long userId,
            Long couponId
    ) {
        public CouponCommand.Issue toCommand() {
            return new CouponCommand.Issue(userId, couponId);
        }
    }
}

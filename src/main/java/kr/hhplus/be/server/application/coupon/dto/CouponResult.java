package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

public record CouponResult() {


    public record Issued(
            Long issuedCouponId,
            Long userId,
            Long couponId,
            CouponStatus status,
            LocalDateTime expiredAt
    ) {}
}

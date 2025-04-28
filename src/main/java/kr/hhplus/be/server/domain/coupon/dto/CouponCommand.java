package kr.hhplus.be.server.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponCommand() {

    public record Find(
            Long couponId
    ) {}

    public record Use(
            Long userId,
            Long couponId
    ) {}

    public record Issue(
            Long userId,
            Long couponId
    ) {
    }

    public record Save(
            Long userId,
            Long couponId
    ) {}

    public record ChangeExpiredAt
    (
            Long userId,
            Long couponId,
            LocalDateTime expiredAt
    ) {}
}

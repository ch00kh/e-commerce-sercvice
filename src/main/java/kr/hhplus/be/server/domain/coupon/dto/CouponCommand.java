package kr.hhplus.be.server.domain.coupon.dto;

import java.time.LocalDateTime;

public record CouponCommand() {

    public record Find(
            Long couponId
    ) {}

    public record Use(
            Long userId,
            Long couponId,  // 사용하려는 쿠폰 ID로 변경
            Long orderId
    ) {}

    public record Apply(
            Long userId,
            Long couponId
    ) {
    }

    public record Save(
            Long userId,
            Long couponId
    ) {}

    public record ChangeExpiredAt(
            Long userId,
            Long couponId,
            LocalDateTime expiredAt
    ) {}

}

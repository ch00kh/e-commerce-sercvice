package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record CouponResult() {

    @Builder
    public record Issued(
            Long id,
            Long userId,
            Long couponId,
            CouponStatus status,
            LocalDateTime expiredAt
    ) {}
}

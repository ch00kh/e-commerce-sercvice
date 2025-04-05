package kr.hhplus.be.server.api.coupon.dto;

import lombok.Builder;

@Builder
public record CouponRequest(
    Long couponId
) {
}

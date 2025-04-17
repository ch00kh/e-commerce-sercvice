package kr.hhplus.be.server.interfaces.coupon.dto;

import lombok.Builder;

@Builder
public record CouponRequest(
    Long couponId
) {
}

package kr.hhplus.be.server.api.coupon.dto;

import lombok.Builder;

@Builder
public record CouponResponse(
    Long couponId,
    String status
) {

}

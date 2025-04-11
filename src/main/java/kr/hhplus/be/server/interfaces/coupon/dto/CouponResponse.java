package kr.hhplus.be.server.interfaces.coupon.dto;

import lombok.Builder;

@Builder
public record CouponResponse(
    Long couponId,
    String status
) {

}

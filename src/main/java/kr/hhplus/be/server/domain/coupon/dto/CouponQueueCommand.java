package kr.hhplus.be.server.domain.coupon.dto;

public record CouponQueueCommand() {

    public record Dequeue(
            Long couponId,
            Long quantity
    ) {}

}

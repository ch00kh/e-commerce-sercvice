package kr.hhplus.be.server.domain.coupon.event;

public record CouponEvent() {
    
    public record Use(
        Long userId,
        Long orderId,
        Long couponId,
        Long issuedCouponId,
        Long discountPrice
    ) {}

    public record Apply(
        Long couponId,
        Long userId
    ) {}
}

package kr.hhplus.be.server.domain.coupon.event;

public record CouponEvent() {

    public record UseCoupon(
            Long userId,
            Long orderId,
            Long couponId,
            Long issuedCouponId,
            Long discountPrice
    ) {}
}

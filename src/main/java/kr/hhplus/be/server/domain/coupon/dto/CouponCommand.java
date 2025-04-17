package kr.hhplus.be.server.domain.coupon.dto;

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
            Long couponId,
            Long discountPrice
    ) {}
}

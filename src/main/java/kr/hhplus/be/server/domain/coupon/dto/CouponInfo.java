package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import lombok.Builder;

import java.time.LocalDateTime;

public record CouponInfo() {

    @Builder
    public record CouponAggregate(
            Long couponId,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime usedAt,
            LocalDateTime expiredAt

    ) {
        public static CouponAggregate from(Coupon coupon, IssuedCoupon issuedCoupon) {
            return CouponAggregate.builder()
                    .couponId(coupon.getId())
                    .discountPrice(coupon.getDiscountPrice())
                    .status(issuedCoupon.getStatus())
                    .usedAt(issuedCoupon.getUsedAt())
                    .expiredAt(issuedCoupon.getExpiredAt())
                    .build();
        }
    }
}

package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;

import java.time.LocalDateTime;

public record CouponInfo() {

    public record CouponAggregate(
            Long couponId,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime usedAt,
            LocalDateTime expiredAt

    ) {
        public static CouponAggregate from() {
            return new CouponAggregate(null, null, null, null, null);
        }
        public static CouponAggregate from(Coupon coupon, IssuedCoupon issuedCoupon) {
            return new CouponAggregate(
                    coupon.getId(),
                    coupon.getDiscountPrice(),
                    issuedCoupon.getStatus(),
                    issuedCoupon.getUsedAt(),
                    issuedCoupon.getExpiredAt()
            );
        }

    }
}

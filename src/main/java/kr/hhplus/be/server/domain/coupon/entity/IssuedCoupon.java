package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.domain.BaseCreatedTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssuedCoupon extends BaseCreatedTimeEntity {

    private Long id;
    private Long userId;
    private Long couponId;
    private CouponStatus status;
    private LocalDateTime usedAt;
    private LocalDateTime expiredAt;

    public void use() {
        if (this.status != CouponStatus.ISSUED) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        this.status = CouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}

package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseCreatedTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_coupon_id", columnList = "couponId")
})
@AllArgsConstructor
public class IssuedCoupon extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Column
    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public IssuedCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = CouponStatus.ISSUED;
        this.expiredAt = LocalDate.now().plusDays(30).atStartOfDay();
    }


    public void use() {
        if (this.status != CouponStatus.ISSUED) {
            throw new GlobalException(ErrorCode.NOT_STATUS_ISSUED_COUPON);
        }
        this.status = CouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void expireCoupon() {
        this.status = CouponStatus.EXPIRED;
    }

    public void changeExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}

package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long discountPrice;

    @Column(nullable = false)
    private Long quantity;

    public Coupon(Long discountPrice, Long quantity) {
        this.discountPrice = discountPrice;
        this.quantity = quantity;
    }

    public Coupon issue() {
        if (this.quantity <= 0) {
            throw new GlobalException(ErrorCode.OUT_OF_STOCK_COUPON);
        }

        quantity--;
        return this;
    }

}

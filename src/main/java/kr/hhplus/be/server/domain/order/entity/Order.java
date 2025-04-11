package kr.hhplus.be.server.domain.order.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseTimeEntity {

    private Long id;
    private Long userId;
    private Long issuedCouponId;
    private OrderStatus status;
    private Long totalAmount;
    private Long discountAmount;
    private Long paymentAmount;

    @Builder
    public Order(Long userId, Long issuedCouponId, Long totalAmount) {
        this.userId = userId;
        this.issuedCouponId = issuedCouponId;
        this.status = OrderStatus.CREATED;
        this.totalAmount = totalAmount;
        this.discountAmount = 0L;
        this.paymentAmount = totalAmount;
    }


    public void useCoupon(Long issuedCouponId, Long discountAmount) {
        this.issuedCouponId = issuedCouponId;

        if (totalAmount < discountAmount) {
            this.discountAmount = totalAmount;
            paymentAmount = 0L;
        }
        if (totalAmount - discountAmount >= 0) {
            this.discountAmount = discountAmount;
            paymentAmount = totalAmount - discountAmount;
        }
    }

    public Order pay() {
        this.status = OrderStatus.PAYED;
        return this;
    }
}

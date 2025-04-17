package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`order`")
//        indexes = {
//        @Index(name = "idx_user_id", columnList = "userId"),
//        @Index(name = "idx_coupon_id", columnList = "issuedCouponId")
//})
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long issuedCouponId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Long discountAmount;

    @Column(nullable = false)
    private Long paymentAmount;

    
    public Order(Long userId, Long totalAmount) {
        this.userId = userId;
        this.status = OrderStatus.CREATED;
        this.totalAmount = totalAmount;
        this.discountAmount = 0L;
        this.paymentAmount = totalAmount;
    }

    public Order(Long userId, Long issuedCouponId, Long totalAmount) {
        this.userId = userId;
        this.status = OrderStatus.CREATED;
        this.totalAmount = totalAmount;
        this.discountAmount = 0L;
        this.issuedCouponId = issuedCouponId;
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

        log.info("totalAmount");
    }

    public Order pay() {
        this.status = OrderStatus.PAYED;
        return this;
    }

}

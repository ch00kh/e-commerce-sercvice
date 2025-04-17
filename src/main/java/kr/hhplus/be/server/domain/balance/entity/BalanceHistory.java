package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(indexes = {
//        @Index(name = "idx_balance_id", columnList = "balanceId"),
//        @Index(name = "idx_issuedCoupon_id", columnList = "issuedCouponId")
//})
public class BalanceHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long balanceId;

    @Column
    private Long issuedCouponId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public BalanceHistory(Long balanceId, Long amount, TransactionType transactionType) {
        this.balanceId = balanceId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public BalanceHistory(Long balanceId, Long issuedCouponId, Long amount, TransactionType transactionType) {
        this.balanceId = balanceId;
        this.issuedCouponId = issuedCouponId;
        this.amount = amount;
        this.transactionType = transactionType;
    }
}

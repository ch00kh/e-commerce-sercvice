package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor

@AllArgsConstructor
@Table(indexes = @Index(name = "idx_order_id", columnList = "orderId"))
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    @Column
    private LocalDateTime paidAt;

    public Payment(Long orderId, Long amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public Payment pay(Long amount) {
        this.paidAt = LocalDateTime.now();
        this.amount -= amount;
        if (this.amount < 0) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        if (this.amount == 0) {
            this.status = PaymentStatus.PAYED;
        }
        return this;
    }
}


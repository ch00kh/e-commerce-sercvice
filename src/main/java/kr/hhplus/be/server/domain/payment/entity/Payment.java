package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
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

    @Builder
    public Payment(Long orderId, Long amount) {
        this.orderId = orderId;
        this.status = PaymentStatus.PENDING;
    }

    public Payment pay(Long amount) {
        this.paidAt = LocalDateTime.now();
        this.amount = amount;
        this.status = PaymentStatus.PAYED;
        return this;
    }
}


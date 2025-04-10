package kr.hhplus.be.server.domain.payment.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTimeEntity {

    private Long id;
    private Long orderId;
    private PaymentStatus status;
    private Long amount;
    private LocalDateTime paidAt;

    @Builder
    public Payment(Long orderId, Long amount) {
        this.orderId = orderId;
        this.status = PaymentStatus.PENDING;
        this.paidAt = LocalDateTime.now();
    }
}


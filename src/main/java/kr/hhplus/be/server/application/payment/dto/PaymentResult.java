package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record PaymentResult() {

    @Builder
    public record Pay(
            Long id,
            Long orderId,
            PaymentStatus status,
            Long amount,
            LocalDateTime paidAt
    ) {}

}

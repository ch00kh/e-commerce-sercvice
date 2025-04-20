package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult() {

    public record Pay(
            Long paymentId,
            Long orderId,
            Long balance,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            Long amount,
            LocalDateTime paidAt
    ) {}

}

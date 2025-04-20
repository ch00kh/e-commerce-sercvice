package kr.hhplus.be.server.interfaces.payment.dto;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;

public record PaymentResponse(
        Long orderId,
        String orderStatus,
        Long paymentId,
        String paymentStatus
) {
    public static PaymentResponse from(PaymentResult.Pay result) {
        return new PaymentResponse(
                result.orderId(),
                result.orderStatus().name(),
                result.paymentId(),
                result.paymentStatus().name()
        );
    }
}

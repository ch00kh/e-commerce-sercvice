package kr.hhplus.be.server.api.payment.dto;

public record PaymentResponse(
        Long orderId,
        String status
) {
}

package kr.hhplus.be.server.api.payment.dto;

public record PaymentRequest(
        Long userId,
        Long orderId
) {
}

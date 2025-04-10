package kr.hhplus.be.server.interfaces.payment.dto;

public record PaymentRequest(
        Long userId,
        Long orderId
) {
}

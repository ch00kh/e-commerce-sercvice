package kr.hhplus.be.server.interfaces.payment.dto;

public record PaymentResponse(
        Long orderId,
        String status
) {
}

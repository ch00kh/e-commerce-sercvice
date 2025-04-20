package kr.hhplus.be.server.interfaces.payment.dto;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;

public record PaymentRequest(
        Long orderId,
        Long amount
) {
    public PaymentCriteria.Pay toCriteria() {
        return new PaymentCriteria.Pay(orderId, amount);
    }
}

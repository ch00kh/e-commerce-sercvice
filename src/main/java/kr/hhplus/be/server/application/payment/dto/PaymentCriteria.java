package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;

public record PaymentCriteria() {

    public record Pay(
            Long orderId,
            Long amount
    ) {
        public PaymentCommand.FindOrder toCommand() {
            return new PaymentCommand.FindOrder(orderId);
        }
    }
}

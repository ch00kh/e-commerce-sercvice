package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;

public record PaymentCriteria() {

    public record Pay(
            Long paymentId
    ) {
        public PaymentCommand.Find toCommand() {
            return new PaymentCommand.Find(paymentId);
        }
    }
}

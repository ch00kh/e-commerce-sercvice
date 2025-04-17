package kr.hhplus.be.server.domain.payment.dto;

import lombok.Builder;

public record PaymentCommand() {

    
    public record Save(
            Long orderId,
            Long amount
    ) {}

    public record Find(
            Long paymentId
    ) {}


    public record Pay(
            Long paymentId,
            Long paymentAmount
    ) {}
}

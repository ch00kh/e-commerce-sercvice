package kr.hhplus.be.server.domain.payment.dto;

import lombok.Builder;

public record PaymentCommand() {

    @Builder
    public record Pay(
            Long orderId,
            Long amount
    ) {}

}

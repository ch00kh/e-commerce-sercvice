package kr.hhplus.be.server.domain.payment.dto;

public record PaymentCommand() {

    public record Create(
            Long orderId,
            Long amount
    ) {}

    public record FindOrder(
            Long orderId
    ) {}

    public record Pay(
            Long paymentId,
            Long paymentAmount
    ) {}
}

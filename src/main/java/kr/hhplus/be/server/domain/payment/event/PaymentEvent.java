package kr.hhplus.be.server.domain.payment.event;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;

public record PaymentEvent() {

    public record PaymentCompleted(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {
    }
}

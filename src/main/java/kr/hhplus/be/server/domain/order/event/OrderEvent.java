package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;

public record OrderEvent() {

    public record OrderComplete(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {}
}

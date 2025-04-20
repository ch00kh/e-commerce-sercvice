package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;

public record OrderInfo() {

    public record Create(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {}

    public record Best(
            Long productOptionId,
            Long totalSaleQuantity
    ) {}
}

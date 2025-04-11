package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import lombok.Builder;

public record OrderInfo() {

    @Builder
    public record Create(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
    }
}

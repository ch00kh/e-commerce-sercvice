package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;

import java.util.List;

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

    public record OrderCreate(
            Long orderId,
            Long userId,
            Long couponId,
            List<OrderCommand.OrderItem> orderItems
    ) {}

    public record OrderCouponApply(
            Long orderId,
            Long userId,
            Long couponId,
            Long issuedCouponId,
            OrderStatus status,
            Long totalAmount,
            Long discountPrice,
            Long paymentAmount
    ) {}

    public record OrderHold(

    ) {}
}

package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;

import java.util.List;

public record OrderCommand() {

    public record Create(
            Long userId,
            List<OrderItem> orderItems
    ) {}

    public record OrderItem (
            Long productOptionId,
            Long unitPrice,
            Long quantity
    ) {}

    public record HoldOrder(
            Long orderId,
            Long productOptionId
    ) {}

    public record UseCoupon(
            Long orderId,
            Long couponId,
            Long discountPrice
    ) {
        public static UseCoupon toCommand(Long orderId, Long couponId, Long discountPrice) {
            return new UseCoupon(orderId, couponId, discountPrice);
        }
    }

    public record Find(
            Long orderId
    ) {}

    public record Send(
            Long id,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {}

    public record FindBest(
            Integer days,
            Integer limit
    ) {}


}

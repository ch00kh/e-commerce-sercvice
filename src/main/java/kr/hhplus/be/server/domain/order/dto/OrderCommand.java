package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import lombok.Builder;

import java.util.List;

public record OrderCommand() {

    @Builder
    public record Create(
            Long userId,
            Long issuedCouponId,
            List<OrderItem> orderItems
    ) {}

    @Builder
    public record OrderItem (
            Long productOptionId,
            Long unitPrice,
            Integer quantity
    ) {}

    public record HoldOrder(
            Long productOptionId
    ) {}

    @Builder
    public record UseCoupon(
            Long orderId,
            Long couponId,
            Long discountPrice
    ) {
        public static UseCoupon toCommand(Long orderId, Long couponId, Long discountPrice) {
            return UseCoupon.builder()
                    .orderId(orderId)
                    .couponId(couponId)
                    .discountPrice(discountPrice)
                    .build();
        }
    }

    public record Find(
            Long orderId
    ) {}

    @Builder
    public record Send(
            Long id,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {}
}

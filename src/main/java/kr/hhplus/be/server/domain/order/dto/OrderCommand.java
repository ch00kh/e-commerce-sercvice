package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;

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

    public record OrderItemList (
            List<OrderItem> orderItems
    ) {
        public static OrderItemList toCommand(List<OrderItem> orderItems) {
            return new OrderItemList(orderItems);
        }
    }

    public record handleOrders(
            Long orderId,
            List<ProductInfo.OptionDetail> stockDetails
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

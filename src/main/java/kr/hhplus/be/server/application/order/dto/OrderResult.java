package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import lombok.Builder;

public record OrderResult() {

    @Builder
    public record Create(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
        public static Create from(OrderInfo.Create orderInfo) {
            return Create.builder()
                    .orderId(orderInfo.orderId())
                    .userId(orderInfo.userId())
                    .status(orderInfo.status())
                    .totalAmount(orderInfo.totalAmount())
                    .discountAmount(orderInfo.discountAmount())
                    .paymentAmount(orderInfo.paymentAmount())
                    .build();
        }
    }
}

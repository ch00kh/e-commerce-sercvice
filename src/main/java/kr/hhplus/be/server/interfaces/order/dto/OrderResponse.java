package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import lombok.Builder;

@Builder
public record OrderResponse(
) {

    @Builder
    public record Create(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
        public static Create from(OrderResult.Create result) {
            return Create.builder()
                    .orderId(result.orderId())
                    .userId(result.userId())
                    .status(result.status())
                    .totalAmount(result.totalAmount())
                    .discountAmount(result.discountAmount())
                    .paymentAmount(result.paymentAmount())
                    .build();
        }
    }
}

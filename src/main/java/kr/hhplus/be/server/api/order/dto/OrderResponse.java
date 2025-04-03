package kr.hhplus.be.server.api.order.dto;

import lombok.Builder;

@Builder
public record OrderResponse(
        Long orderId,
        Long userId,
        Long productId,
        String status,
        Long totalAmount,
        Long discountAmount,
        Long paymentAmount
) {}

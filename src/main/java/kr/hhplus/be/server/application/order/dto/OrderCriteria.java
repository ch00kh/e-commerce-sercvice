package kr.hhplus.be.server.application.order.dto;

import lombok.Builder;

import java.util.List;

public record OrderCriteria() {

    @Builder
    public record Order(
            Long userId,
            Long productId,
            List<OrderItem> items,
            Long couponId
    ) {}

    @Builder
    public record OrderItem(
            Long productOptionId,
            Integer quantity
    ) {}


}

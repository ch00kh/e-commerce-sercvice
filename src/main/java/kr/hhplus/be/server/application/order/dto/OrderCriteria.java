package kr.hhplus.be.server.application.order.dto;

import java.util.List;

public record OrderCriteria() {

    public record Create(
            Long userId,
            Long productId,
            List<OrderItem> items,
            Long couponId
    ) {}

    
    public record OrderItem(
            Long productOptionId,
            Integer quantity
    ) {}


}

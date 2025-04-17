package kr.hhplus.be.server.application.order.dto;

import java.util.List;

public record OrderCriteria() {

    
    public record Order(
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

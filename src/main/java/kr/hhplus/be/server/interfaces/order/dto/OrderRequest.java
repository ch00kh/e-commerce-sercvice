package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.application.order.dto.OrderCriteria;

import java.util.List;


public record OrderRequest() {

    public record Create(
            Long userId,
            Long productId,
            List<Item> items,
            Long couponId
    ) {
        public OrderCriteria.Create toCriteria() {
            return new OrderCriteria.Create(
                    userId,
                    productId,
                    items.stream().map(item -> new OrderCriteria.OrderItem(item.optionId, item.quantity)).toList(),
                    couponId

            );
        }

    }

    public record Item(
            Long optionId,
            Long quantity
    ) {
    }
}

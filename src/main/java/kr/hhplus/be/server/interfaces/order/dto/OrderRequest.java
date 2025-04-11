package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest() {

    public record Order(
            Long userId,
            Long productId,
            List<Item> items,
            Long couponId
    ) {
        public OrderCriteria.Order toCriteria() {
            return OrderCriteria.Order.builder()
                    .userId(userId)
                    .productId(productId)
                    .items(items.stream().map(item -> OrderCriteria.OrderItem.builder()
                                    .productOptionId(item.optionId)
                                    .quantity(item.quantity)
                                    .build())
                            .toList())
                    .build();
        }

    }

    @Builder
    public record Item(
            Long optionId,
            Integer quantity
    ) {
    }
}

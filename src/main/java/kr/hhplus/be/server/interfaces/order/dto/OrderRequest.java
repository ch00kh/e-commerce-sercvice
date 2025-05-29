package kr.hhplus.be.server.interfaces.order.dto;

import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;

import java.util.Comparator;
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
                    items.stream()
                            .map(item -> new OrderCriteria.OrderItem(item.optionId, item.quantity))
                            .sorted(Comparator.comparing(OrderCriteria.OrderItem::productOptionId))
                            .toList(),
                    couponId
            );
        }

        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    userId,
                    couponId,
                    items.stream()
                            .map(item -> new OrderCommand.OrderItem(item.optionId, item.unitPrice, item.quantity))
                            .sorted(Comparator.comparing(OrderCommand.OrderItem::productOptionId))
                            .toList()
            );
        }
    }

    public record Item(
            Long optionId,
            Long quantity,
            Long unitPrice
    ) {
    }
}

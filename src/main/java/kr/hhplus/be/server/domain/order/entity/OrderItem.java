package kr.hhplus.be.server.domain.order.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseTimeEntity {

    private Long id;
    private Long orderId;
    private Long productOptionId;
    private OrderStatus status;
    private Long unitPrice;
    private Integer quantity;

    @Builder
    public OrderItem(Long orderId, Long productOptionId, Long unitPrice, Integer quantity) {
        this.orderId = orderId;
        this.productOptionId = productOptionId;
        this.status = OrderStatus.CREATED;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public OrderItem holdStatus() {
        this.status = OrderStatus.PENDING;
        return this;
    }
}

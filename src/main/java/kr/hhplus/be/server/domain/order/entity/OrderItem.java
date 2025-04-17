package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_order_id", columnList = "orderId"),
        @Index(name = "idx_productOption_id", columnList = "productOptionId")
})
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productOptionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long quantity;

    
    public OrderItem(Long orderId, Long productOptionId, Long unitPrice, Long quantity) {
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

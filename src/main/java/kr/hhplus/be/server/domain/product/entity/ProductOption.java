package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_product_id", columnList = "productId"))
@AllArgsConstructor
public class ProductOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String optionValue;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long stock;

    
    public ProductOption(Long productId, String optionValue, Long price, Long stock) {
        this.productId = productId;
        this.optionValue = optionValue;
        this.price = price;
        this.stock = stock;
    }

    public boolean canPurchase(Long stock) {
        return this.stock - stock >= 0;
    }

    public Long reduceStock(Long quantity) {
        this.stock -= quantity;
        return this.stock;
    }
}

package kr.hhplus.be.server.domain.product.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOption extends BaseTimeEntity {

    private Long id;
    private Long productId;
    private String optionValue;
    private Long price;
    private Long stock;

    @Builder
    public ProductOption(Long id, String optionValue, Long price, Long stock) {
        this.id = id;
        this.optionValue = optionValue;
        this.price = price;
        this.stock = stock;
    }
}

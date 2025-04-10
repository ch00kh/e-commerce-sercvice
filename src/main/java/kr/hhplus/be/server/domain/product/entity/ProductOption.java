package kr.hhplus.be.server.domain.product.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ProductOption extends BaseTimeEntity {

    private Long id;
    private Long productId;
    private String optionValue;
    private Long price;
    private Integer stock;

    @Builder
    public ProductOption(Long id, String optionValue, Long price, Integer stock) {
        this.id = id;
        this.optionValue = optionValue;
        this.price = price;
        this.stock = stock;
    }

    public Integer reduceStock(Integer stock) {
        this.stock = this.stock - stock;

        if (this.stock < 0) {
            log.error("Out Of Stock...");
            this.stock = 0;
        }
        return this.stock;
    }
}

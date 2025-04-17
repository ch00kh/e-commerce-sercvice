package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import lombok.Builder;

import java.util.List;

public record ProductInfo(){

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList of(List<ProductAggregate> productAggregates) {
            return new ProductList(productAggregates);
        }
    }

    @Builder
    public record ProductAggregate(
        Long productId,
        String brand,
        String name,
        List<ProductOption> options
    ) {
        public static ProductAggregate from(Product product, List<ProductOption> productOption) {
            return new ProductAggregate(
                    product.getId(),
                    product.getBrand(),
                    product.getName(),
                    productOption.stream().map(o ->
                            new ProductOption(o.getId(), o.getOptionValue(), o.getPrice(), o.getStock())
                    ).toList()
            );
        }
    }

    @Builder
    public record CheckedStock(
            Long optionId,
            boolean isEnough,
            Integer requestQuantity,
            Integer remainingQuantity
    ) {}

    public record CheckedProductOrder(
            List<CheckedStock> checkStocks
    ) {}
}

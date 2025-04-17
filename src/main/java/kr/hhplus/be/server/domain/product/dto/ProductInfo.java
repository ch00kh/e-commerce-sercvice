package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;

import java.util.List;

public record ProductInfo(){

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList of(List<ProductAggregate> productAggregates) {
            return new ProductList(productAggregates);
        }
    }

    
    public record ProductAggregate(
        Long productId,
        String brand,
        String name,
        List<Option> options
    ) {
        public static ProductAggregate from(Product product, ProductOption productOption) {
            return new ProductAggregate(
                    product.getId(),
                    product.getBrand(),
                    product.getName(),
                    List.of(new Option(productOption.getId(), productOption.getOptionValue(), productOption.getPrice(), productOption.getStock()))
            );
        }
        public static ProductAggregate from(Product product, List<ProductOption> productOption) {
            return new ProductAggregate(
                    product.getId(),
                    product.getBrand(),
                    product.getName(),
                    productOption.stream().map(o ->
                            new Option(o.getId(), o.getOptionValue(), o.getPrice(), o.getStock())
                    ).toList()
            );
        }
    }

    public record Option(
        Long optionId,
        String optionValue,
        Long price,
        Long stock
    ) {}

    public record OptionDetail(
            Long optionId,
            boolean canPurchase,
            Long requestQuantity,
            Long remainingQuantity
    ) {}

    public record Order(
            List<OptionDetail> checkStocks
    ) {}
}

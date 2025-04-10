package kr.hhplus.be.server.interfaces.product.dto;

import kr.hhplus.be.server.application.product.dto.ProductResult;
import lombok.Builder;

import java.util.List;

public record ProductResponse() {

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList from(ProductResult.ProductList result) {
            return new ProductList(result.products().stream().map(ProductAggregate::from).toList());
        }
    }

    @Builder
    public record ProductAggregate(
            Long productId,
            String brand,
            String name,
            List<ProductResponse.Option> options
    ) {
        public static ProductAggregate from(ProductResult.ProductAggregate product) {
            return ProductAggregate.builder()
                    .productId(product.productId())
                    .brand(product.brand())
                    .name(product.name())
                    .options(product.options().stream().map(Option::from).toList())
                    .build();
        }
    }

    @Builder
    public record Option(
            Long optionId,
            String optionValue,
            Long price,
            Long stock
    ) {
        public static Option from(ProductResult.Option option) {
            return Option.builder()
                    .optionId(option.optionId())
                    .optionValue(option.optionValue())
                    .price(option.price())
                    .stock(option.stock())
                    .build();
        }
    }
}

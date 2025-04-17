package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.Builder;

import java.util.List;

public record ProductResult() {

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList from(ProductInfo.ProductList productInfo) {
            return new ProductList(productInfo.products().stream().map(ProductAggregate::from).toList());
        }
    }

    @Builder
    public record ProductAggregate(
            Long productId,
            String brand,
            String name,
            List<Option> options
    ) {
        public static ProductAggregate from(ProductInfo.ProductAggregate product) {
            return ProductAggregate.builder()
                    .productId(product.productId())
                    .brand(product.brand())
                    .name(product.name())
                    .options(product.options().stream().map(o -> Option.builder()
                                    .optionId(o.getId())
                                    .optionValue(o.getOptionValue())
                                    .price(o.getPrice())
                                    .stock(o.getStock())
                                    .build())
                            .toList())
                    .build();
        }
    }

    @Builder
    public record Option(
            Long optionId,
            String optionValue,
            Long price,
            Integer stock
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

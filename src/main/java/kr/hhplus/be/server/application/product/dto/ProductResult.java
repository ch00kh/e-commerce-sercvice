package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.List;

public record ProductResult() {

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList from(ProductInfo.ProductList productInfo) {
            return new ProductList(productInfo.products().stream().map(ProductAggregate::from).toList());
        }
        public static ProductList from(List<ProductInfo.ProductAggregate> productInfo) {
            return new ProductList(productInfo.stream().map(ProductAggregate::from).toList());
        }
    }

    public record ProductAggregate(
            Long productId,
            String brand,
            String name,
            List<Option> options
    ) {
        public static ProductAggregate from(ProductInfo.ProductAggregate product) {
            return new ProductAggregate(
                    product.productId(),
                    product.brand(),
                    product.name(),
                    product.options().stream().map(o -> new Option(
                                    o.optionId(),
                                    o.optionValue(),
                                    o.price(),
                                    o.stock()
                            ))
                            .toList()
            );
        }
    }

    public record Option(
            Long optionId,
            String optionValue,
            Long price,
            Long stock
    ) {
        public static Option from(ProductResult.Option option) {
            return new Option(
                    option.optionId(),
                    option.optionValue(),
                    option.price(),
                    option.stock()
            );
        }

    }
}

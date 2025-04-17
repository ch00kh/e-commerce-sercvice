package kr.hhplus.be.server.interfaces.product.dto;

import kr.hhplus.be.server.application.product.dto.ProductResult;

import java.util.List;

public record ProductResponse() {

    public record ProductList(
            List<ProductAggregate> products
    ) {
        public static ProductList from(ProductResult.ProductList result) {
            return new ProductList(result.products().stream().map(ProductAggregate::from).toList());
        }
    }

    
    public record ProductAggregate(
            Long productId,
            String brand,
            String name,
            List<ProductResponse.Option> options
    ) {
        public static ProductAggregate from(ProductResult.ProductAggregate product) {
            return new ProductAggregate(
                    product.productId(),
                    product.brand(),
                    product.name(),
                    product.options().stream().map(Option::from).toList()
            );
        }
    }

    
    public record Option(
            Long optionId,
            String optionValue,
            Long price,
            Integer stock
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

package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    ProductService productService;

    public ProductResult.ProductList findAll() {

        return ProductResult.ProductList.from(productService.findAll());
    }

    public ProductResult.ProductAggregate findProduct(ProductCriteria.Find criteria) {

        return ProductResult.ProductAggregate.from(productService.findProduct(criteria.toCommand()));
    }
}

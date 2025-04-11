package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    ProductService productService;

    @Transactional(readOnly = true)
    public ProductResult.ProductList findAll() {
        return ProductResult.ProductList.from(productService.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResult.ProductAggregate findProduct(ProductCriteria.Find criteria) {
        return ProductResult.ProductAggregate.from(productService.findProduct(criteria.toCommand()));
    }
}

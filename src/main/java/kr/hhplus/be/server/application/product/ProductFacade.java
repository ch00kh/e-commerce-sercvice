package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    ProductService productService;
    OrderService orderService;

    public ProductResult.ProductList findAll() {

        return ProductResult.ProductList.from(productService.findAll());
    }

    public ProductResult.ProductAggregate findProduct(ProductCriteria.Find criteria) {

        return ProductResult.ProductAggregate.from(productService.findProduct(criteria.toCommand()));
    }

    public ProductResult.ProductList findBest(ProductCriteria.FindBest criteria) {

        List<OrderInfo.Best> orderInfo = orderService.findBestSelling(new OrderCommand.FindBest(criteria.days(), criteria.limit()));
        List<ProductCommand.FindByProductOptionId> optionIds = orderInfo.stream().map(info -> new ProductCommand.FindByProductOptionId(info.productOptionId())).toList();

        return ProductResult.ProductList.from(optionIds.stream().map(id -> productService.findProductByOptionId(id)).toList());
    }
}

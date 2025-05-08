package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final OrderService orderService;

    /**
     * 전체 상품 조회
     */
    @Transactional
    public ProductResult.ProductList findAll() {
        return ProductResult.ProductList.from(productService.findAll());
    }

    /**
     * 상품 정보 조회
     */
    @Cacheable(value = CacheType.CacheName.PRODUCT, key = "'productId:' + #criteria.productId()")
    public ProductResult.ProductAggregate findProduct(ProductCriteria.Find criteria) {
        return ProductResult.ProductAggregate.from(productService.findProduct(criteria.toCommand()));
    }

    /**
     * 인기 판매 상품 조회
     */
    @Cacheable(value = CacheType.CacheName.BEST_PRODUCT, key = "'best:days:' + #criteria.days() + ':limit:' + #criteria.limit()")
    @Transactional
    public ProductResult.ProductList findBest(ProductCriteria.FindBest criteria) {

        List<OrderInfo.Best> orderInfo = orderService.findBestSelling(new OrderCommand.FindBest(criteria.days(), criteria.limit()));
        List<ProductCommand.FindByProductOptionId> optionIds = orderInfo.stream().map(info -> new ProductCommand.FindByProductOptionId(info.productOptionId())).toList();

        return ProductResult.ProductList.from(optionIds.stream().map(id -> productService.findProductByOptionId(id)).toList());
    }

    /**
     * 인기 판매 상품 캐시 갱신
     */
    @CachePut(value = CacheType.CacheName.BEST_PRODUCT, key = "'best:days:' + #criteria.days() + ':limit:' + #criteria.limit()")
    @Transactional
    public ProductResult.ProductList refreshBestProductCache(ProductCriteria.FindBest criteria) {

        List<OrderInfo.Best> orderInfo = orderService.findBestSelling(new OrderCommand.FindBest(criteria.days(), criteria.limit()));
        List<ProductCommand.FindByProductOptionId> optionIds = orderInfo.stream().map(info -> new ProductCommand.FindByProductOptionId(info.productOptionId())).toList();

        return ProductResult.ProductList.from(optionIds.stream().map(id -> productService.findProductByOptionId(id)).toList());
    }
}

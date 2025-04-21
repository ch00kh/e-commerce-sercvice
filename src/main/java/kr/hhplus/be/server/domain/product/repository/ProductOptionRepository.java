package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductOption;

import java.util.List;

public interface ProductOptionRepository {

    ProductOption findById(Long optionId);

    List<ProductOption> findByProductId(Long productId);

    ProductOption save(ProductOption productOption);

    void deleteAll();
}

package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductOption;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository {

    Optional<ProductOption> findById(Long optionId);

    List<ProductOption> findByProductId(Long productId);

    ProductOption save(ProductOption productOption);
}

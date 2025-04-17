package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductOption;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository {

    Optional<ProductOption> findById(Long optionId);

    List<ProductOption> findByProductId(Long productId);
}

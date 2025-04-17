package kr.hhplus.be.server.infra.persistence.product;

import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryImpl implements ProductOptionRepository {

    private final ProductOptionJpaRepository jpaRepository;

    @Override
    public Optional<ProductOption> findById(Long optionId) {
        return jpaRepository.findById(optionId);
    }

    @Override
    public List<ProductOption> findByProductId(Long productId) {
        return jpaRepository.findByProductId(productId);
    }

    @Override
    public ProductOption save(ProductOption productOption) {
        return jpaRepository.save(productOption);
    }
}

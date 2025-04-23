package kr.hhplus.be.server.infra.persistence.product;

import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryImpl implements ProductOptionRepository {

    private final ProductOptionJpaRepository jpaRepository;

    @Override
    public ProductOption findById(Long optionId) {
        return jpaRepository.findById(optionId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
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

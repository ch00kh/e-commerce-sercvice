package kr.hhplus.be.server.infra.persistence.product;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public Optional<Product> findById(Long productId) {
        return jpaRepository.findById(productId);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll();
    }
}

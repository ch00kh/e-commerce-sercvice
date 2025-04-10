package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository {

    List<ProductOption> findByProductId(Long id);
}

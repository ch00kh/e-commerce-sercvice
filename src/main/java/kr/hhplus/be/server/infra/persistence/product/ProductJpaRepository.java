package kr.hhplus.be.server.infra.persistence.product;

import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}

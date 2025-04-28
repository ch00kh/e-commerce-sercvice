package kr.hhplus.be.server.infra.persistence.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o From ProductOption o WHERE o.id = :optionId")
    Optional<ProductOption> findByIdWithPessimisticLock(Long optionId);
}

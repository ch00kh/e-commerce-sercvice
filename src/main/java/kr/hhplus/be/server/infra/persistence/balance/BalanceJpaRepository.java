package kr.hhplus.be.server.infra.persistence.balance;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithOptimisticLock(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithPessimisticLock(Long userId);
}

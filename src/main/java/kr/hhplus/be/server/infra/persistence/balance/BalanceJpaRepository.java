package kr.hhplus.be.server.infra.persistence.balance;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

}

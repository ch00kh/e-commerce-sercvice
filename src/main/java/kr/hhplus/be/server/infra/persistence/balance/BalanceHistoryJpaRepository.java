package kr.hhplus.be.server.infra.persistence.balance;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryJpaRepository extends JpaRepository<BalanceHistory, Long> {
}

package kr.hhplus.be.server.infra.persistence.balance;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceHistoryJpaRepository extends JpaRepository<BalanceHistory, Long> {

    List<BalanceHistory> findByBalanceId(Long balanceId);

}

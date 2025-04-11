package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceHistoryRepository {

    BalanceHistory save(BalanceHistory balanceHistory);
}

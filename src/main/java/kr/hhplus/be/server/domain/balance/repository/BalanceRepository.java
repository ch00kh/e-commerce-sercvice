package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;

public interface BalanceRepository {

    Balance findByUserId(Long id);

    Balance findByUserIdWithOptimisticLock(Long userId);

    Balance save(Balance balance);
}

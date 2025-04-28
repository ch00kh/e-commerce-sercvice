package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;

public interface BalanceRepository {

    Balance findByUserId(Long userId);

    Balance findByUserIdWithOptimisticLock(Long userId);

    Balance findByUserIdWithPessimisticLock(Long userId);

    Balance save(Balance balance);
}

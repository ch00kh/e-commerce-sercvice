package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;

public interface BalanceRepository {

    Balance findByUserId(Long id);

    Balance save(Balance balance);
}

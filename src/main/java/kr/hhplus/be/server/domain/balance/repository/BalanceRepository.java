package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;

import java.util.Optional;

public interface BalanceRepository {

    Optional<Balance> findByUserId(Long id);

    Balance save(Balance balance);
}

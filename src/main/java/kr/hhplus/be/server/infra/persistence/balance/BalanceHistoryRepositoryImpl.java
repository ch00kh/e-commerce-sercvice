package kr.hhplus.be.server.infra.persistence.balance;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BalanceHistoryRepositoryImpl implements BalanceHistoryRepository {

    private final BalanceHistoryJpaRepository jpaRepository;

    @Override
    public BalanceHistory save(BalanceHistory balanceHistory) {
        return jpaRepository.save(balanceHistory);
    }

    @Override
    public List<BalanceHistory> findByBalanceId(Long userId) {
        return jpaRepository.findByBalanceId(userId);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}

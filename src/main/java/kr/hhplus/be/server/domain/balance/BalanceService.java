package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.entity.TransactionType;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    /**
     * 잔액 조회
     */
    @Transactional(readOnly = true)
    public Balance find(BalanceCommand.Find command) {

        return balanceRepository.findByUserId(command.userId());
    }

    /**
     * 잔액 충전
     */
    @Transactional
    public Balance charge(BalanceCommand.Charge command) {

        Balance balance = balanceRepository.findByUserIdWithOptimisticLock(command.userId());

        balance.charge(command.amount());

        balanceHistoryRepository.save(new BalanceHistory(balance.getId(), command.amount(), TransactionType.CHARGE));

        return balance;
    }

    /**
     * 잔액 차감
     */
    @Transactional
    public Balance reduce(BalanceCommand.Reduce command) {

        Balance balance = balanceRepository.findByUserIdWithPessimisticLock(command.userId());

        balanceHistoryRepository.save(new BalanceHistory(balance.getId(), command.issuedCouponId(), command.paymentAmount(), TransactionType.USE));

        return balance.reduce(command.paymentAmount());
    }

    /**
     * 잔고 생성
     */
    public Balance create(BalanceCommand.Create command) {

        return balanceRepository.save(new Balance(command.userId(), 0L));
    }
}

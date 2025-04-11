package kr.hhplus.be.server.domain.balance;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.entity.TransactionType;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;


    @Transactional(readOnly = true)
    public Balance findBalance(BalanceCommand.Find command) {

        return balanceRepository.findByUserId(command.userId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public Balance charge(BalanceCommand.Charge command) {

        Balance balance = balanceRepository.findByUserId(command.userId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        balance.charge(command.amount());

        balanceHistoryRepository.save(
                BalanceHistory.builder()
                        .balanceId(balance.getId())
                        .amount(command.amount())
                        .transactionType(TransactionType.CHARGE)
                        .build()
        );

        return balance;
    }

    @Transactional
    public Balance reduceBalance(BalanceCommand.Reduce command) {

        Balance balance = balanceRepository.findByUserId(command.userId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return balance.reduce(command.paymentAmount());
    }
}

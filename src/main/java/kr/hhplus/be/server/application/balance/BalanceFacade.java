package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;
import kr.hhplus.be.server.application.balance.dto.BalanceResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;

    /**
     * 잔액 충전
     */
    @Transactional
    public BalanceResult.UserBalance charge(BalanceCriteria.Charge criteria) {

        return BalanceResult.UserBalance.from(balanceService.charge(criteria.toCommand()));
    }

    /**
     * 잔액 조회
     */
    @Transactional(readOnly = true)
    public BalanceResult.UserBalance findBalance(BalanceCriteria.Find criteria) {

        return BalanceResult.UserBalance.from(balanceService.find(criteria.toCommand()));
    }

}

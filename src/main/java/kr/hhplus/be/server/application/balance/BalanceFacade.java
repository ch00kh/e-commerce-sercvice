package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;
import kr.hhplus.be.server.application.balance.dto.BalanceResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;

    /**
     * 잔액 충전
     */
    public BalanceResult.UserBalance charge(BalanceCriteria.Charge criteria) {
        return BalanceResult.UserBalance.from(balanceService.charge(criteria.toCommand()));
    }

    /**
     * 잔액 조회
     */
    public BalanceResult.UserBalance findBalance(BalanceCriteria.Find criteria) {
        return BalanceResult.UserBalance.from(balanceService.find(criteria.toCommand()));
    }

}

package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;
import kr.hhplus.be.server.application.balance.dto.BalanceResult;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public BalanceResult.UserBalance findBalance(BalanceCriteria.Find criteria) {

        userService.findByUserId(UserCriteria.Find.toCommand(criteria.userId()));
        return BalanceResult.UserBalance.from(balanceService.findBalance(criteria.toCommand()));
    }

    @Transactional
    public BalanceResult.UserBalance charge(BalanceCriteria.Charge criteria) {

        userService.findByUserId(UserCriteria.Find.toCommand(criteria.userId()));
        return BalanceResult.UserBalance.from(balanceService.charge(criteria.toCommand()));

    }
}

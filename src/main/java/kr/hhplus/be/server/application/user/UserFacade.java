package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final BalanceService balanceService;

    public UserResult.Create createUser(UserCriteria.Create criteria) {

        User user = userService.create(criteria.toCommand());
        Balance balance = balanceService.create(new BalanceCommand.Create(user.getId()));

        return new UserResult.Create(user.getId(), user.getName(), balance.getBalance());
    }

}

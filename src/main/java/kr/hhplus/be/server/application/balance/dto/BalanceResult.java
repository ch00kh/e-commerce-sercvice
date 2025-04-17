package kr.hhplus.be.server.application.balance.dto;

import kr.hhplus.be.server.domain.balance.entity.Balance;

public record BalanceResult() {

    
    public record UserBalance(
            Long id,
            Long userId,
            Long balance
    ) {
        public static UserBalance from(Balance balance) {
            return new UserBalance(balance.getId(), balance.getUserId(), balance.getBalance());
        }

    }
}

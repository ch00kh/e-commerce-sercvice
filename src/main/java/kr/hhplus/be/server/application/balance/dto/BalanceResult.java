package kr.hhplus.be.server.application.balance.dto;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import lombok.Builder;

public record BalanceResult() {

    @Builder
    public record UserBalance(
            Long id,
            Long userId,
            Long balance
    ) {
        public static UserBalance from(Balance balance) {
            return UserBalance.builder()
                    .id(balance.getId())
                    .userId(balance.getUserId())
                    .balance(balance.getBalance())
                    .build();
        }

    }
}

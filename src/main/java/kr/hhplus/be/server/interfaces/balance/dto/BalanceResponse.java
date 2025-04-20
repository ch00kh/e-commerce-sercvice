package kr.hhplus.be.server.interfaces.balance.dto;

import kr.hhplus.be.server.application.balance.dto.BalanceResult;
import lombok.Builder;


public record BalanceResponse() {

    public record UserBalance(
            Long userId,
            Long amount
    ) {
        public static UserBalance from(BalanceResult.UserBalance result) {
            return new UserBalance(
                    result.userId(),
                    result.balance()
            );
        }
    }
}

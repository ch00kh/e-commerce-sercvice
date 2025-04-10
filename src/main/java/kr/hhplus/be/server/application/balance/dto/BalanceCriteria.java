package kr.hhplus.be.server.application.balance.dto;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import lombok.Builder;

public record BalanceCriteria() {

    @Builder
    public record Find(
            Long userId
    ) {
        public BalanceCommand.Find toCommand() {
            return new BalanceCommand.Find(userId);
        }
    }

    @Builder
    public record Charge(
            Long userId,
            Long amount
    ) {
        public BalanceCommand.Charge toCommand() {
            return new BalanceCommand.Charge(userId, amount);
        }
    }

}

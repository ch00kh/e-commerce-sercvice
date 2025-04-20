package kr.hhplus.be.server.application.balance.dto;

import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;

public record BalanceCriteria() {

    public record Find(
            Long userId
    ) {
        public BalanceCommand.Find toCommand() {
            return new BalanceCommand.Find(userId);
        }
    }

    public record Charge(
            Long userId,
            Long amount
    ) {
        public BalanceCommand.Charge toCommand() {
            return new BalanceCommand.Charge(userId, amount);
        }
    }

    public record Create(
            Long userId
    ) {}



}

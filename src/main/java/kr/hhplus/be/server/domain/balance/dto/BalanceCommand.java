package kr.hhplus.be.server.domain.balance.dto;

import lombok.Builder;

public record BalanceCommand() {

    
    public record Find(
            Long userId
    ) {
        public static Find of(Long userId) {
            return new Find(userId);
        }
    }

    
    public record Charge(
            Long userId,
            Long amount
    ) {
        public static Charge of(Long userId, Long amount) {
            return new Charge(userId, amount);
        }
    }

    public record Reduce(
            Long userId,
            Long paymentAmount,
            Long issuedCouponId
    ) {}

    public record Create(
            Long userId
    ) {}
}


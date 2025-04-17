package kr.hhplus.be.server.interfaces.balance.dto;

import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;

public record BalanceRequest() {

    public record Find() {
        public static BalanceCriteria.Find toCriteria(Long userId) {
            return new BalanceCriteria.Find(userId);
        }
    }

    public record Charge(
            @Min(value = 0, message = "양수만 입렵 가능합니다.")
            Long amount
    ) {
        public BalanceCriteria.Charge toCriteria(Long userId) {
            return new BalanceCriteria.Charge(userId, amount);
        }
    }
}

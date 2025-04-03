package kr.hhplus.be.server.api.balance.dto;

import lombok.Builder;

@Builder
public record BalanceResponse(
        Long userId,
        Long amount
) {
}

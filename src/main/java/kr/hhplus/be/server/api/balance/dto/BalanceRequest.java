package kr.hhplus.be.server.api.balance.dto;

import lombok.Builder;

@Builder
public record BalanceRequest(
        Long amount
) {
}

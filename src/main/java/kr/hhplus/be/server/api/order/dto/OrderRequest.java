package kr.hhplus.be.server.api.order.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        Long userId,
        Long productId,
        List<Item> items,
        Long couponId
) {

    @Builder
    public record Item(
            Long productDetailId,
            Integer quantity
    ) {}
}

package kr.hhplus.be.server.api.product.dto;

import lombok.Builder;

@Builder
public record BestProductResponse(
        Long productId,
        String brand,
        String name,
        int totalOrders,
        Option option
) {

    @Builder
    public record Option(
        Long detailId,
        String optionValue,
        Long price,
        Long stock
    ){
    }
}

package kr.hhplus.be.server.api.product.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductResponse(
        Long productId,
        String brand,
        String name,
        List<Options> options
) {

    @Builder
    public record Options(
            Long productDetailId,
            String optionValue,
            Long price,
            Long stock
    ){}
}

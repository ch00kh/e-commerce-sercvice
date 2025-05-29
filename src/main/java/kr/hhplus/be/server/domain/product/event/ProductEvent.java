package kr.hhplus.be.server.domain.product.event;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.List;

public record ProductEvent() {
    public record ReduceStock(
            Long orderId,
            List<ProductInfo.OptionDetail> optionDetails
    ) {}
}

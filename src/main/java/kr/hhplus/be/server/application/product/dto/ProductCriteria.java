package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.dto.ProductCommand;

public record ProductCriteria() {

    public record Find(
            Long productId
    ) {
        public ProductCommand.Find toCommand() {
            return new ProductCommand.Find(productId);
        }
    }

    public record FindBest(
            Integer days,
            Integer limit
    ) {}
}

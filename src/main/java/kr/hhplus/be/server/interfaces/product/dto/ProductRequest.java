package kr.hhplus.be.server.interfaces.product.dto;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;

public record ProductRequest() {

    public record Find(
            Long productId
    ) {
        public static ProductCriteria.Find toCriteria(Long productId) {
            return new ProductCriteria.Find(productId);
        }
    }

}

package kr.hhplus.be.server.domain.product.dto;

public record ProductCommand() {

    public record Find(
            Long productId
    ) {}
}

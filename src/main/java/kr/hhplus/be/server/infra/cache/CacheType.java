package kr.hhplus.be.server.infra.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    PRODUCT(CacheName.PRODUCT, 60),
    BEST_PRODUCT(CacheName.BEST_PRODUCT, 30)
    ;

    public static class CacheName {
        public static final String PRODUCT = "product";
        public static final String BEST_PRODUCT = "product-best";
    }

    private final String value;
    private final int ttlMinutes;

}

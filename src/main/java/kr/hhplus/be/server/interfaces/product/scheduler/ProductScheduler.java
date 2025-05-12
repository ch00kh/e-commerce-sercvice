package kr.hhplus.be.server.interfaces.product.scheduler;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductFacade productFacade;

    /**
     * 20분마다 실행 (30분 캐시 타임아웃 기준 10분 전)
     */
    @Scheduled(fixedDelay = 20 * 60 * 1000)
    public void scheduleCachingProductBest() {
        productFacade.refreshBestProductCache(new ProductCriteria.FindBest(3, 5));
        productFacade.refreshBestProductCache(new ProductCriteria.FindBest(7, 5));
        productFacade.refreshBestProductCache(new ProductCriteria.FindBest(30, 5));
    }
}

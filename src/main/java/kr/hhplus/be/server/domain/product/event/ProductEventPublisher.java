package kr.hhplus.be.server.domain.product.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final ApplicationEventPublisher publisher;

    /**
     */
    public void publishReduceProductEvent(ProductEvent.ReduceStock event) {
        log.info("재고 차감 이벤트 발행 : {}", event);
        publisher.publishEvent(event);
    }

}

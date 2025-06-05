package kr.hhplus.be.server.infra.event.order;

import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final OrderEventProducer producer;

    /**
     * 주문 생성 이벤트 발행
     */
    public void publish(OrderEvent.OrderCreate event) {
        log.info("주문 생성 이벤트 발행 : {}", event);
        producer.send(event);
    }

    /**
     * 주문 완료 이벤트 발행
     */
    public void publish(OrderEvent.OrderComplete event) {
        publisher.publishEvent(event);
    }
}

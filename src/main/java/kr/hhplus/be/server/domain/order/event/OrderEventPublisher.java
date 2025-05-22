package kr.hhplus.be.server.domain.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishOrderComplete(OrderEvent.OrderComplete event) {
        publisher.publishEvent(event);
    }
}

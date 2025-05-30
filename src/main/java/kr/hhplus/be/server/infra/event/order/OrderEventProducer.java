package kr.hhplus.be.server.infra.event.order;

import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(OrderEvent.OrderCreate event) {
        kafkaTemplate.send(EventType.Topic.ORDER_CREATE, event.orderId().toString(), event);
    }
}

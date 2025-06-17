package kr.hhplus.be.server.domain.product.event;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ProductService productService;

    /**
     * 주문 생성 이벤트 수신 - 재고 차감
     */
    @KafkaListener(topics = EventType.Topic.ORDER_CREATE, groupId = EventType.GroupId.ORDER_SERVICE, concurrency = "4")
    public void handleOrderCreateEvent(OrderEvent.OrderCreate event, Acknowledgment ack) {
        productService.reduceStock(new OrderCommand.Reduce(
                event.orderId(),
                event.orderItems().stream()
                        .map(i -> new OrderCommand.OrderItem(
                                i.productOptionId(),
                                i.unitPrice(),
                                i.quantity()
                                )
                        ).toList()
                )
        );
        ack.acknowledge();
    }
}

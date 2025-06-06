package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.product.event.ProductEvent;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    /**
     * 주문 완료 이벤트 수신
     */
    @KafkaListener(topics = EventType.Topic.ORDER_COMPLETE, groupId = EventType.GroupId.ORDER_SERVICE, concurrency = "4")
    public void handleOrderCompleteEvent(OrderEvent.OrderComplete event) {
        orderService.sendOrder(OrderCommand.Send.of(event));
    }

    /**
     * 주문 상태 변경 이벤트 수신
     */
    @KafkaListener(topics = EventType.Topic.ORDER_STATE, groupId = EventType.GroupId.ORDER_SERVICE, concurrency = "4")
    public void handleHoldOrderEvent(ProductEvent.ReduceStock event) {
        orderService.changeOrderState(
                new OrderCommand.handleOrders(
                        event.orderId(),
                        event.optionDetails()
                )
        );
    }
}

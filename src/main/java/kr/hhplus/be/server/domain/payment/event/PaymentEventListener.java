package kr.hhplus.be.server.domain.payment.event;

import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    /**
     * 주문 생성 이벤트 수신 - 결제 정보 생성
     */
    @KafkaListener(topics = EventType.Topic.ORDER_CREATE, groupId = EventType.GroupId.PAYMENT, concurrency = "4")
    public void handleOrderCreateEvent(OrderEvent.OrderCreate event, Acknowledgment ack) {
        paymentService.paymentCreate(
                new PaymentCommand.Create(
                        event.orderId(),
                        event.orderItems().stream().mapToLong(item -> item.unitPrice() * item.quantity()).sum()
                )
        );

        ack.acknowledge();
    }

}

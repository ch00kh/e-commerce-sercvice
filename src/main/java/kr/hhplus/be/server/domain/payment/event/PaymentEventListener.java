package kr.hhplus.be.server.domain.payment.event;

import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    /**
     * 주문 생성 이벤트 수신 - 결제 정보 생성
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreateEvent(OrderEvent.OrderCreate event) {
        Long initialAmount = event.orderItems().stream()
                .mapToLong(item -> item.unitPrice() * item.quantity())
                .sum();
        
        paymentService.save(new PaymentCommand.Save(event.orderId(), initialAmount));
    }

    /**
     * 주문 완료 이벤트 수신
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCompleteEvent(OrderEvent.OrderComplete event) {
        paymentService.save(new PaymentCommand.Save(event.orderId(), event.paymentAmount()));
    }

}

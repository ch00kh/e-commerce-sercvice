package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.product.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    /**
     * 주문 완료 이벤트 수신
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleteEvent(OrderEvent.OrderComplete event) {
        orderService.sendOrder(OrderCommand.Send.of(event));
    }

    /**
     * 쿠폰 적용 이벤트 수신
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleUseCouponEvent(CouponEvent.UseCoupon event) {
        orderService.applyCoupon(new OrderCommand.UseCoupon(event.orderId(), event.issuedCouponId(), event.userId()));
    }

    /**
     * 주문 보류 이벤트 수신
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleHoldOrderEvent(ProductEvent.ReduceStock event) {
        orderService.holdOrders(
                new OrderCommand.handleOrders(
                        event.orderId(),
                        event.optionDetails()
                )
        );
    }
}

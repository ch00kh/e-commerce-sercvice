package kr.hhplus.be.server.domain.product.event;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ProductService productService;

    /**
     * 쿠폰 적용 이벤트 수신
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreateEvent(OrderEvent.OrderCreate event) {
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
    }
}

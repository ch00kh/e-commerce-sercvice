package kr.hhplus.be.server.domain.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    /**
     * 주문 완료 이벤트 발행
     */
    public void publishOrderCompleteEvent(OrderEvent.OrderComplete event) {
        log.info("주문 완료 이벤트 발행 : {}", event);
        publisher.publishEvent(event);
    }

    /**
     * 주문 생성 이벤트 발행
     */
    public void publishOrderCreateEvent(OrderEvent.OrderCreate event) {
        log.info("주문 생성 이벤트 발행 : {}", event);
        publisher.publishEvent(event);
    }

    /**
     * 주문 쿠폰 적용 이벤트 발행
     */
    public void publishCouponApplyEvent(OrderEvent.OrderCouponApply event) {
        log.info("주문 쿠폰 적용 이벤트 발행 : {}", event);
        publisher.publishEvent(event);
    }
}

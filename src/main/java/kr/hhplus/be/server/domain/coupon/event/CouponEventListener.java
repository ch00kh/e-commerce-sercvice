package kr.hhplus.be.server.domain.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventListener {

    private final CouponService couponService;

    /**
     * 주문 생성 이벤트 수신 - 쿠폰 사용
     */
    @KafkaListener(topics = EventType.Topic.ORDER_CREATE, groupId = EventType.GroupId.COUPON_SERVICE, concurrency = "4")
    public void handleOrderCreateEvent(OrderEvent.OrderCreate event, Acknowledgment ack) {
        couponService.use(new CouponCommand.Use(event.userId(), event.couponId(), event.orderId()));
        ack.acknowledge();
    }

    /**
     * 선착수 쿠폰 발급 이벤트 수신
     */
    @KafkaListener(topics = EventType.Topic.COUPON_APPLY, groupId = EventType.GroupId.COUPON_SERVICE, concurrency = "4")
    public void handleCouponIssueEvent(CouponEvent.Apply command, Acknowledgment ack) {
        couponService.issue(new CouponCommand.Apply(command.userId(), command.couponId()));
        ack.acknowledge();
    }
}

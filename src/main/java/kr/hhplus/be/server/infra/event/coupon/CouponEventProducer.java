package kr.hhplus.be.server.infra.event.coupon;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.global.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(CouponEvent.Apply event) {
        kafkaTemplate.send(EventType.Topic.COUPON_APPLY, event.couponId().toString(), event);
    }

    public void send(CouponEvent.Use event) {
        kafkaTemplate.send(EventType.Topic.COUPON_USE, event.couponId().toString(), event);
    }

}

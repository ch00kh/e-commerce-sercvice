package kr.hhplus.be.server.infra.event.coupon;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventPublisherImpl implements CouponEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final CouponEventProducer producer;

    @Override
    public void publish(CouponEvent.Apply event) {
        producer.send(event);
    }

    @Override
    public void publish(CouponEvent.Use event) {
        producer.send(event);
    }
}

package kr.hhplus.be.server.domain.coupon.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUseCouponEvent(CouponEvent.UseCoupon event) {
        log.info("쿠폰 사용 이벤트 발행 : {}", event);
        publisher.publishEvent(event);
    }
}

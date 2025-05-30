package kr.hhplus.be.server.domain.coupon.event;

public interface CouponEventPublisher {

    void publish(CouponEvent.Apply event);

    void publish(CouponEvent.Use event);

}

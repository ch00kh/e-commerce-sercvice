package kr.hhplus.be.server.interfaces.coupon.scheduler;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponFacade couponFacade;


    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleExpireCoupon() {
        couponFacade.expireCoupon();
    }
}

package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    /**
     * 쿠폰 발급
     */
    public CouponResult.Enqueue firstComeFirstIssue(CouponCriteria.Issue criteria) {

        // 쿠폰 캐싱
        CouponInfo.Cache coupon = couponService.findCoupon(new CouponCommand.Find(criteria.couponId()));

        // 쿠폰 대기열 등록
        couponService.enqueue(criteria.toCommand());

        return new CouponResult.Enqueue(coupon.id());

    }

    /**
     * 쿠폰 만료
     */
    public void expireCoupon() {
        couponService.expireCoupon();
    }

}

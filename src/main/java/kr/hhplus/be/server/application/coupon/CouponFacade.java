package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.dto.CouponQueueCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponQueueInfo;
import kr.hhplus.be.server.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    /**
     * 쿠폰 대기열 등록
     */
    public CouponResult.Enqueue apply(CouponCriteria.Enqueue criteria) {

        // 쿠폰 캐싱
        CouponInfo.Cache coupon = couponService.getCoupon(new CouponCommand.Find(criteria.couponId()));

        // 쿠폰 대기열 등록
        couponService.apply(criteria.toCommand());

        return new CouponResult.Enqueue(coupon.id());

    }

    /**
     * 쿠폰 만료
     */
    public void expireCoupon() {
        couponService.expireCoupon();
    }

    /**
     * 대기열로부터 선착순 쿠폰 발급 저장
     */
    @Transactional
    public void processIssuedCouponApply() {

        CouponQueueInfo.Keys keysInfo = couponService.getCouponKeys();

        for (String couponKey : keysInfo.couponKeys()) {
            Long couponId = Long.parseLong(couponKey.substring((CacheType.CacheName.COUPON + "::couponId:").length()));

            CouponInfo.Cache coupon = couponService.getCoupon(new CouponCommand.Find(couponId));

            CouponQueueInfo.UserIds couponQueueInfo = couponService.dequeueUsers(new CouponQueueCommand.Dequeue(couponId, coupon.quantity()));
            couponQueueInfo.userIds().forEach(userId -> couponService.issue(new CouponCommand.Issue(userId, couponId)));
        }

    }
}

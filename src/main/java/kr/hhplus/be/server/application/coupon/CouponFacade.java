package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    /**
     * 선착순 쿠폰 발급
     */
    public CouponResult.Issued firstComeFirstIssue(CouponCriteria.Issue criteria) {

        IssuedCoupon issuedCoupon = couponService.issue(criteria.toCommand());

        return new CouponResult.Issued(
                issuedCoupon.getId(),
                issuedCoupon.getUserId(),
                issuedCoupon.getCouponId(),
                issuedCoupon.getStatus(),
                issuedCoupon.getExpiredAt()
        );
    }

    /**
     * 쿠폰 만료
     */
    public void expireCoupon() {
        couponService.expireCoupon();
    }
}

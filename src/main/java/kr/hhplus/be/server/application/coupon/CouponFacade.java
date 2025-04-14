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

    public CouponResult.Issued firstComeFirstIssue(CouponCriteria.Issue criteria) {

        IssuedCoupon issuedCoupon = couponService.issue(criteria.toCommand());

        return CouponResult.Issued.builder()
                .id(issuedCoupon.getId())
                .userId(issuedCoupon.getUserId())
                .couponId(issuedCoupon.getCouponId())
                .status(issuedCoupon.getStatus())
                .expiredAt(issuedCoupon.getExpiredAt())
                .build();
    }
}

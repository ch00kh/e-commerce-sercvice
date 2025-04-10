package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;

    @Transactional
    public CouponResult.Issued firstComeFirstIssue(CouponCriteria.Issue criteria) {

        // 잔여 쿠폰 조회 및 생성
        Coupon coupon = couponService.issue(criteria.toCommand());

        // 쿠폰 저장 (기발급 검증)
        IssuedCoupon issuedCoupon = couponService.save(new CouponCommand.Save(criteria.userId(), coupon.getId(), coupon.getDiscountPrice()));

        return CouponResult.Issued.builder()
                .id(issuedCoupon.getId())
                .userId(issuedCoupon.getUserId())
                .couponId(issuedCoupon.getCouponId())
                .status(issuedCoupon.getStatus())
                .expiredAt(issuedCoupon.getExpiredAt())
                .build();
    }
}

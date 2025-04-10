package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.domain.coupon.dto.CouponInfo.CouponAggregate;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    public CouponAggregate useCoupon(CouponCommand.Use command) {

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        issuedCoupon.use();

        return CouponAggregate.from(coupon, issuedCoupon);
    }
}

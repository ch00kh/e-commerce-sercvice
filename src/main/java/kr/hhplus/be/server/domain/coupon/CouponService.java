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
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.coupon.dto.CouponInfo.CouponAggregate;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public CouponAggregate use(CouponCommand.Use command) {

        if (command.couponId() == null) {
            return CouponAggregate.from();
        }

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        issuedCoupon.use();

        return CouponAggregate.from(coupon, issuedCoupon);
    }

    @Transactional
    public IssuedCoupon issue(CouponCommand.Issue command) {

        // 잔여 쿠폰 조회 및 쿠폰 수량 차감
        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        coupon.issue();

        // 기발급 검증 및 쿠폰 저장
        issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .ifPresent(issuedCoupon -> {
                    throw new GlobalException(ErrorCode.ALREADY_ISSUED_COUPON);
                });

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

}

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

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        issuedCoupon.use();

        return CouponAggregate.from(coupon, issuedCoupon);
    }

    @Transactional
    public Coupon issue(CouponCommand.Issue command) {

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        if (coupon.getQuantity() <= 0) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }

        coupon.issue();

        return coupon;
    }

    @Transactional
    public IssuedCoupon save(CouponCommand.Save command) {

        issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .ifPresent(coupon -> {
                    throw new GlobalException(ErrorCode.BAD_REQUEST);
                });

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }
}

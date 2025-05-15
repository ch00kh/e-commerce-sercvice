package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.coupon.dto.CouponInfo.CouponAggregate;
import static kr.hhplus.be.server.infra.cache.CacheType.CacheName.COUPON;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    /**
     * 쿠폰 사용
     */
    @Transactional
    public CouponAggregate use(CouponCommand.Use command) {

        if (command.couponId() == null) {
            return CouponAggregate.from();
        }

        Coupon coupon = couponRepository.findById(command.couponId());

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId());

        issuedCoupon.use();

        return CouponAggregate.from(coupon, issuedCoupon);
    }

    /**
     * 쿠폰 발급
     */
    @Transactional
    public IssuedCoupon issue(CouponCommand.Issue command) {

        // 잔여 쿠폰 조회 및 쿠폰 수량 차감
        Coupon coupon = couponRepository.findByIdWithOptimisticLock(command.couponId());

        coupon.issue();

        // 기발급 검증
        if (issuedCouponRepository.existsByUserIdAndCouponId(command.userId(), command.couponId())) {
            throw new GlobalException(ErrorCode.ALREADY_ISSUED_COUPON);
        }

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    /**
     * 쿠폰 만료 처리
     */
    @Transactional
    public void expireCoupon() {
        List<IssuedCoupon> expiredCoupons = issuedCouponRepository.findExpiredCoupons();
        expiredCoupons.forEach(IssuedCoupon::expireCoupon);
    }

    /**
     * 발급 쿠폰 만료일 변경
     */
    @Transactional
    public void changeExpiredAt(CouponCommand.ChangeExpiredAt command) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId());
        issuedCoupon.changeExpiredAt(command.expiredAt());
    }

    /**
     * 쿠폰 찾기
     */
    @Cacheable(value = COUPON, key = "'couponId:' + #command.couponId()")
    public CouponInfo.Cache findCoupon(CouponCommand.Find command) {
        Coupon coupon = couponRepository.findById(command.couponId());
        return new CouponInfo.Cache(coupon);
    }

    /**
     * 쿠폰 발급 대기열 등록
     */
    public void enqueue(CouponCommand.Issue command) {
        couponRepository.enqueue(command.couponId(), command.userId());
    }

}

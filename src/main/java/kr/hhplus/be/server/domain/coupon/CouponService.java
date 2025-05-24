package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.dto.CouponQueueCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponQueueInfo;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.global.aop.DistributedLock;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.coupon.dto.CouponInfo.CouponAggregate;
import static kr.hhplus.be.server.infra.cache.CacheType.CacheName.COUPON;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponEventPublisher eventPublisher;

    /**
     * 쿠폰 사용
     */
    @Transactional
    public CouponAggregate use(CouponCommand.Use command) {

        if (command.couponId() == null) {
            return CouponAggregate.from();
        }

        // 사용자가 해당 쿠폰을 발급받았는지 확인
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId());

        Coupon coupon = couponRepository.findById(command.couponId());

        issuedCoupon.use();

        eventPublisher.publishUseCouponEvent(
                new CouponEvent.UseCoupon(
                        command.userId(),
                        command.orderId(),
                        coupon.getId(),
                        issuedCoupon.getId(),
                        coupon.getDiscountPrice()
                )
        );

        return CouponAggregate.from(coupon, issuedCoupon);
    }

    /**
     * 쿠폰 발급
     */
    @Transactional
    @DistributedLock(value = "order:#{#command.couponId}", waitTime = 60, leaseTime = 30)
    public void issue(CouponCommand.Issue command) {

        // 잔여 쿠폰 조회 및 쿠폰 수량 차감
        Coupon coupon = couponRepository.findByIdWithOptimisticLock(command.couponId());

        try {
            coupon.issue();

            issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));

        } catch (GlobalException e) {
            if (e.getErrorCode().equals(ErrorCode.OUT_OF_STOCK_COUPON)) {
                log.error("Coupon Issue Error : {}, Coupon Id: {}", e.getMessage(), command.couponId());
                couponRepository.deleteQueue(command.couponId());
            }
        }
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
    public CouponInfo.Cache getCoupon(CouponCommand.Find command) {
        Coupon coupon = couponRepository.findById(command.couponId());
        return new CouponInfo.Cache(coupon);
    }

    /**
     * 쿠폰 발급 대기열 등록
     */
    public void apply(CouponCommand.Issue command) {
        couponRepository.enqueue(command.couponId(), command.userId());
    }

    /**
     * 캐시되어있는 모든 쿠폰 가져오기
     */
    public CouponQueueInfo.Keys getCouponKeys() {
        return new CouponQueueInfo.Keys(couponRepository.getCouponKeys());
    }

    /**
     * 쿠폰 대기열에서 사용자 ID 목록 가져오기
     */
    public CouponQueueInfo.UserIds dequeueUsers(CouponQueueCommand.Dequeue command) {
        return new CouponQueueInfo.UserIds(couponRepository.dequeueUsers(command.couponId(), command.quantity()));
    }
}

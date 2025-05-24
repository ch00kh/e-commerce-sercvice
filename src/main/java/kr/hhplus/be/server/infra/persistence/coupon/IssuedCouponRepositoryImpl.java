package kr.hhplus.be.server.infra.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
 public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository jpaRepository;

    @Override
    public IssuedCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        return jpaRepository.findByUserIdAndCouponId(userId, couponId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return jpaRepository.save(issuedCoupon);
    }

    @Override
    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return jpaRepository.existsByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public List<IssuedCoupon> findExpiredCoupons() {
        return jpaRepository.findByStatusAndExpiredAtBefore(CouponStatus.ISSUED, LocalDateTime.now());
    }

    @Override
    public IssuedCoupon findById(Long issuedCouponId) {
        return jpaRepository.findById(issuedCouponId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

}

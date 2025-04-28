package kr.hhplus.be.server.infra.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {

    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<IssuedCoupon> findByStatusAndExpiredAtBefore(CouponStatus status, LocalDateTime expiredAtBefore);
}

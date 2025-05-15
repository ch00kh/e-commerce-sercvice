package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.List;
import java.util.Set;

public interface CouponRepository {

    Coupon findById(Long couponId);

    Coupon findByIdWithOptimisticLock(Long couponId);

    Coupon save(Coupon coupon);

    void enqueue(Long couponId, Long userId);

    Long getCouponQueueSize(Long couponId);
    
    Set<String> getCouponKeys();
    
    List<Long> dequeueUsers(Long couponId, Long limit);

}

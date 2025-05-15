package kr.hhplus.be.server.infra.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository jpaRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Coupon findById(Long couponId) {
        return jpaRepository.findById(couponId).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    @Override
    public Coupon findByIdWithOptimisticLock(Long couponId) {
        return jpaRepository.findByIdWithOptimisticLock(couponId);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return jpaRepository.save(coupon);
    }

    @Override
    public void enqueue(Long couponId, Long value) {
        String key = CacheType.CacheName.COUPON_QUEUE + couponId;
        redisTemplate.opsForZSet().addIfAbsent(key, String.valueOf(value), System.currentTimeMillis());
        redisTemplate.expire(key, CacheType.COUPON_QUEUE.getTtlMinutes(), TimeUnit.MINUTES);
    }

    @Override
    public Long getCouponQueueSize(Long couponId) {
        String key = CacheType.CacheName.COUPON_QUEUE + couponId;
        return redisTemplate.opsForZSet().zCard(key);
    }

}

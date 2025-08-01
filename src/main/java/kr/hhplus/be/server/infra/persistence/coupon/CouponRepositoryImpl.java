package kr.hhplus.be.server.infra.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.infra.cache.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public void enqueue(Long couponId, Long userId) {
        String key = CacheType.CacheName.COUPON_QUEUE + couponId;
        redisTemplate.opsForZSet().addIfAbsent(key, String.valueOf(userId), System.currentTimeMillis());
        redisTemplate.expire(key, CacheType.COUPON_QUEUE.getTtlMinutes(), TimeUnit.MINUTES);
    }

    @Override
    public Long getCouponQueueSize(Long couponId) {
        String key = CacheType.CacheName.COUPON_QUEUE + couponId;
        return redisTemplate.opsForZSet().zCard(key);
    }
    
    @Override
    public Set<String> getCouponKeys() {
        return redisTemplate.keys(CacheType.CacheName.COUPON + "::*");
    }
    
    @Override
    public List<Long> dequeueUsers(Long couponId, Long limit) {
        String queueKey = CacheType.CacheName.COUPON_QUEUE + couponId;

        // 쿠폰 대기열에서 시간 순으로 쿠폰수량 만큼 사용자 ID 가져오기
        Set<ZSetOperations.TypedTuple<String>> userQueue = redisTemplate.opsForZSet().rangeWithScores(queueKey, 0, limit - 1);

        return userQueue.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .collect(Collectors.toList());

    }

    @Override
    public Boolean deleteQueue(Long couponId) {
        String key = CacheType.CacheName.COUPON_QUEUE + couponId;
        return redisTemplate.delete(key);
    }

}

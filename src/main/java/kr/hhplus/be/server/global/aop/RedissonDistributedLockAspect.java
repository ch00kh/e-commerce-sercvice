package kr.hhplus.be.server.global.aop;

import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.global.util.CustomSpelExpressionParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedissonDistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(DistributedLock)")
    public Object handleRedissonPubSubLock(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        List<String> lockNames = CustomSpelExpressionParser.parseKey(joinPoint);
        RLock lock = generateLocks(lockNames);

        try {
            boolean isLock = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

            if (!isLock) {
                throw new GlobalException(ErrorCode.LOCK_ACQUIRED_FAILED);
            }

            return joinPoint.proceed();

        } catch (GlobalException e) {
            log.error("Already Locked Cannot Acquire Lock: {}", e.getMessage());
            throw e;

        } catch (Throwable e) {
            log.error("Failed to acquire lock: {}", e.getMessage());
            throw new GlobalException(ErrorCode.INTERNAL_LOCK_ERROR);

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    protected RLock generateLocks(List<String> lockNames) {
        RLock[] lockArray = lockNames.stream()
                .map(redissonClient::getLock)
                .toArray(RLock[]::new);

        return redissonClient.getMultiLock(lockArray);
    }
}

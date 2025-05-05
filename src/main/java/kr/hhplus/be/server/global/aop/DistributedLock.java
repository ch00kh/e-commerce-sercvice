package kr.hhplus.be.server.global.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 분산 환경에서 동시성 제어를 위한 Lock annotation.
 *
 * <pre>
 * 사용 예시
 * 단일 락: {@code order:stock:#{#criteria.productOptionId}}
 * 멀티 락: {@code order:stock:#{#criteria.items[*].productOptionId}}
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * Lock Name
     * <pre>
     * 사용 예시
     * 단일 락: {@code order:stock:#{#criteria.productOptionId}}
     * 멀티 락: {@code order:stock:#{#criteria.items[*].productOptionId}}
     * </pre>
     */
    String value() default "";

    /**
     * 락 획득 대기 최대 시간
     */
    long waitTime() default 5L;

    /**
     * 락 최대 유지 시간(ms)
     */
    long leaseTime() default 3L;

    /**
     * waitTime and leaseTime 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}

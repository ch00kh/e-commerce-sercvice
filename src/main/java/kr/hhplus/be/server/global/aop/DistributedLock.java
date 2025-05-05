package kr.hhplus.be.server.global.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * Lock Name
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

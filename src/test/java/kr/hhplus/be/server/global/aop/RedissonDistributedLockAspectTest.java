package kr.hhplus.be.server.global.aop;

import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.global.util.CustomSpelExpressionParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedissonDistributedLockAspectTest {

    @Mock
    RedissonClient redissonClient;

    @Mock
    ProceedingJoinPoint joinPoint;

    @Mock
    MethodSignature signature;

    @Mock
    RLock lock;

    MockedStatic<CustomSpelExpressionParser> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(CustomSpelExpressionParser.class);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Nested
    @DisplayName("락 획득에 성공한다.")
    class AcquireLock {

        @Test
        @DisplayName("단일 락을 획득 후 메서드 실행 후 락을 해제한다.")
        void DistributedSingleLockSuccess() throws Throwable {
            // Arrange
            Method method = TestService.class.getMethod("doSomething", TestCriteria.SingleLockCriteria.class);

            RedissonDistributedLockAspect aspect = new RedissonDistributedLockAspect(redissonClient) {
                @Override
                protected RLock generateLocks(List<String> lockNames) {
                    return lock;
                }
            };

            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getMethod()).thenReturn(method);
            mockedStatic.when(() -> CustomSpelExpressionParser.parseKey(joinPoint)).thenReturn(List.of("test:lock:1"));
            when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(joinPoint.proceed()).thenReturn("success");

            // Act
            Object result = aspect.handleRedissonPubSubLock(joinPoint);

            // Assert
            assertThat(result).isEqualTo("success");
            verify(joinPoint).proceed();
            verify(lock).unlock();
        }

        @Test
        @DisplayName("멀티 락을 획득 후 메서드 실행 후 락을 해제한다.")
        void DistributedMultiLockSuccess() throws Throwable {
            // Arrange
            Method method = TestService.class.getMethod("doSomething", TestCriteria.MultiLockCriteria.class);

            RedissonDistributedLockAspect aspect = new RedissonDistributedLockAspect(redissonClient) {
                @Override
                protected RLock generateLocks(List<String> lockNames) {
                    return lock;
                }
            };

            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getMethod()).thenReturn(method);
            mockedStatic.when(() -> CustomSpelExpressionParser.parseKey(joinPoint)).thenReturn(List.of("test:lock:1", "test:lock:2"));
            when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(joinPoint.proceed()).thenReturn("success");

            // Act
            Object result = aspect.handleRedissonPubSubLock(joinPoint);

            // Assert
            assertThat(result).isEqualTo("success");
            verify(joinPoint).proceed();
            verify(lock).unlock();
        }
    }

    @Nested
    @DisplayName("락 획득에 실패한다.")
    class FailAcquireLock {

        @Test
        @DisplayName("이미 락이 걸려 있어 락 획득에 실패한다.")
        void DistributedSingleLockSuccess() throws Throwable {
            // Arrange
            Method method = TestService.class.getMethod("doSomething", TestCriteria.SingleLockCriteria.class);

            RedissonDistributedLockAspect aspect = new RedissonDistributedLockAspect(redissonClient) {
                @Override
                protected RLock generateLocks(List<String> lockNames) {
                    return lock;
                }
            };

            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getMethod()).thenReturn(method);
            mockedStatic.when(() -> CustomSpelExpressionParser.parseKey(joinPoint)).thenReturn(List.of("test:lock:1"));
            when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);
            when(lock.isHeldByCurrentThread()).thenReturn(false);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> aspect.handleRedissonPubSubLock(joinPoint));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOCK_ACQUIRED_FAILED);
        }
    }
}

package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] CouponService")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @InjectMocks
    private CouponService couponService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Long ISSUED_COUPON_ID;

    private Coupon COUPON;
    private IssuedCoupon ISSUED_COUPON;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        COUPON_ID = 1L;
        ISSUED_COUPON_ID = 111L;

        COUPON = new Coupon(1000L, 100L);
        ISSUED_COUPON = new IssuedCoupon(USER_ID, COUPON_ID);
    }

    @Test
    @DisplayName("쿠폰 미사용")
    void unuseCoupon_() {

        // Arrange
        CouponCommand.Use command = new CouponCommand.Use(USER_ID, null);

        // Act
        CouponInfo.CouponAggregate actualInfo = couponService.use(command);

        // Assert
        assertThat(actualInfo.couponId()).isNull();
        assertThat(actualInfo.discountPrice()).isNull();
        assertThat(actualInfo.status()).isNull();
        assertThat(actualInfo.usedAt()).isNull();
        assertThat(actualInfo.expiredAt()).isNull();
    }

    @Nested
    @DisplayName("쿠폰 적용")
    class useCoupon {

        @Test
        @DisplayName("쿠폰 적용시 발급된 쿠폰 상태가 변경된다.")
        void useCoupon_ok() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(COUPON);
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(ISSUED_COUPON);

            // Act
            CouponInfo.CouponAggregate actualInfo = couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);

            assertThat(actualInfo.status()).isEqualTo(CouponStatus.USED);
            assertThat(actualInfo.usedAt()).isNotNull();
        }

        @Test
        @DisplayName("등록되지 않은 쿠폰으로 쿠폰을 적용할 수 없다.")
        void useCoupon_coupon_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("사용자에게 발급된 쿠폰이 없어 쿠폰을 적용할 수 없다.")
        void useCoupon_issuedCoupon_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(COUPON);
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("발급 상태의 쿠폰이 아니여서 쿠폰을 적용할 수 없다.")
        void useCoupon_notStatusIssuedCoupon() {

            // Arrange
            IssuedCoupon usedIssuedCoupon = new IssuedCoupon(ISSUED_COUPON_ID, USER_ID, COUPON_ID, CouponStatus.USED, null, LocalDateTime.now().plusDays(30));

            when(couponRepository.findById(COUPON_ID)).thenReturn(COUPON);
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(usedIssuedCoupon);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_STATUS_ISSUED_COUPON);
        }
    }

    @Nested
    @DisplayName("쿠폰 발급")
    class issue {

        @Test
        @DisplayName("쿠폰ID와 사용자ID가 유효하다면 쿠폰 발급에 성공한다.")
        void issue_ok() {

            // Arrange
            when(couponRepository.findByIdWithOptimisticLock(COUPON_ID)).thenReturn(COUPON);
            when(issuedCouponRepository.existsByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(false);
            when(issuedCouponRepository.save(any(IssuedCoupon.class))).thenReturn(new IssuedCoupon(USER_ID, COUPON_ID));

            // Act
            IssuedCoupon actual = couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID));

            // Assert
            verify(couponRepository,times(1)).findByIdWithOptimisticLock(COUPON_ID);
            verify(issuedCouponRepository, times(1)).existsByUserIdAndCouponId(USER_ID, COUPON_ID);
            verify(issuedCouponRepository,times(1)).save(any(IssuedCoupon.class));

            assertThat(COUPON.getQuantity()).isEqualTo(99);
            assertThat(actual.getCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
        }

        @Test
        @DisplayName("등록되지 않은 쿠폰은 발급할 수 없다.")
        void issue_NotFound() {

            // Arrange
            when(couponRepository.findByIdWithOptimisticLock(COUPON_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findByIdWithOptimisticLock(COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("등록된 쿠폰이 소진되어 더 이상 쿠폰을 발급할 수 없다.")
        void issue_outOfStockCoupon() {

            // Arrange
            Coupon insufficientCoupon1 = new Coupon(1000L, 0L);
            Coupon insufficientCoupon2 = new Coupon(1000L, -1L);

            when(couponRepository.findByIdWithOptimisticLock(1L)).thenReturn(insufficientCoupon1);
            when(couponRepository.findByIdWithOptimisticLock(2L)).thenReturn(insufficientCoupon2);

            // Act & Assert
            GlobalException exception1 = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 1L)));

            verify(couponRepository, times(1)).findByIdWithOptimisticLock(1L);
            assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK_COUPON);

            GlobalException exception2 = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 2L)));

            verify(couponRepository, times(1)).findByIdWithOptimisticLock(2L);
            assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK_COUPON);
        }
    }
}
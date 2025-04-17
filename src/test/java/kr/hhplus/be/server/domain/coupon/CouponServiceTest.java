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
import java.util.Optional;

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
        @DisplayName("[성공] 쿠폰 적용시 상태 변경 (ISSUED -> USED)")
        void useCoupon_ok() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(ISSUED_COUPON));

            // Act
            CouponInfo.CouponAggregate actualInfo = couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);

            assertThat(actualInfo.status()).isEqualTo(CouponStatus.USED);
            assertThat(actualInfo.usedAt()).isNotNull();
        }

        @Test
        @DisplayName("[실패] 쿠폰 적용 -> 쿠폰 없음(NOT_FOUND)")
        void useCoupon_coupon_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("[실패] 쿠폰 적용 -> 발급된 쿠폰 없음(NOT_FOUND)")
        void useCoupon_issuedCoupon_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.use(new CouponCommand.Use(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("[실패] 쿠폰 적용 -> 쿠폰 상태가 ISSUED 가 아님(NOT_STATUS_ISSUED_COUPON)")
        void useCoupon_notStatusIssuedCoupon() {

            // Arrange
            IssuedCoupon usedIssuedCoupon = new IssuedCoupon(ISSUED_COUPON_ID, USER_ID, COUPON_ID, CouponStatus.USED, null, LocalDateTime.now().plusDays(30));

            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(usedIssuedCoupon));

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
        @DisplayName("[성공] 쿠폰 발급")
        void issue_ok() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.empty());
            when(issuedCouponRepository.save(any(IssuedCoupon.class))).thenReturn(new IssuedCoupon(USER_ID, COUPON_ID));

            // Act
            IssuedCoupon actual = couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID));

            // Assert
            verify(couponRepository,times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            verify(issuedCouponRepository,times(1)).save(any(IssuedCoupon.class));

            assertThat(COUPON.getQuantity()).isEqualTo(99);
            assertThat(actual.getCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getStatus()).isEqualTo(CouponStatus.ISSUED);
        }

        @Test
        @DisplayName("[실패] 쿠폰 발급 -> 없는 쿠폰(NOT_FOUND)")
        void issue_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, COUPON_ID)));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("[실패] 쿠폰 발급 -> 수량 부족(OUT_OF_STOCK_COUPON)")
        void issue_outOfStockCoupon() {

            // Arrange
            Coupon insufficientCoupon1 = new Coupon(1L, 1000L, 0L);
            Coupon insufficientCoupon2 = new Coupon(2L, 1000L, -1L);

            when(couponRepository.findById(1L)).thenReturn(Optional.of(insufficientCoupon1));
            when(couponRepository.findById(2L)).thenReturn(Optional.of(insufficientCoupon2));

            // Act & Assert
            GlobalException exception1 = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 1L)));

            verify(couponRepository, times(1)).findById(1L);
            assertThat(exception1.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK_COUPON);

            GlobalException exception2 = assertThrows(GlobalException.class,
                    () -> couponService.issue(new CouponCommand.Issue(USER_ID, 2L)));

            verify(couponRepository, times(1)).findById(2L);
            assertThat(exception2.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK_COUPON);
        }
    }
}
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
    private CouponCommand.Use COMMAND;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        COUPON_ID = 11L;
        ISSUED_COUPON_ID = 111L;

        COUPON = Coupon.builder()
                .id(COUPON_ID)
                .discountPrice(1000L)
                .build();

        ISSUED_COUPON = IssuedCoupon.builder()
                .id(ISSUED_COUPON_ID)
                .userId(USER_ID)
                .couponId(COUPON_ID)
                .status(CouponStatus.ISSUED)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        COMMAND = new CouponCommand.Use(USER_ID, COUPON_ID);
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
            CouponInfo.CouponAggregate actualInfo = couponService.useCoupon(COMMAND);

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);

            assertThat(actualInfo.status()).isEqualTo(CouponStatus.USED);
            assertThat(actualInfo.usedAt()).isNotNull();
            assertThat(actualInfo.couponId()).isEqualTo(COUPON_ID);
        }

        @Test
        @DisplayName("[실패] 쿠폰 적용 -> 쿠폰 없음(NOT_FOUND)")
        void useCoupon_coupon_NotFound() {

            // Arrange
            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.useCoupon(COMMAND));

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
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.useCoupon(COMMAND));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("[실패] 쿠폰 적용 -> 쿠폰 상태가 ISSUED 가 아님(BAD_REQUEST)")
        void useCoupon_BadRequest() {

            // Arrange
            IssuedCoupon usedIssuedCoupon = IssuedCoupon.builder()
                    .id(ISSUED_COUPON_ID)
                    .userId(USER_ID)
                    .couponId(COUPON_ID)
                    .status(CouponStatus.USED)
                    .expiredAt(LocalDateTime.now().plusDays(30))
                    .build();

            when(couponRepository.findById(COUPON_ID)).thenReturn(Optional.of(COUPON));
            when(issuedCouponRepository.findByUserIdAndCouponId(USER_ID, COUPON_ID)).thenReturn(Optional.of(usedIssuedCoupon));

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> couponService.useCoupon(COMMAND));

            // Assert
            verify(couponRepository, times(1)).findById(COUPON_ID);
            verify(issuedCouponRepository, times(1)).findByUserIdAndCouponId(USER_ID, COUPON_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST);
        }

    }
}
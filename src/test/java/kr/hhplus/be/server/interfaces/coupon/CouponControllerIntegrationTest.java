package kr.hhplus.be.server.interfaces.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.application.user.UserFacade;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.interfaces.coupon.controller.CouponController;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] CouponController")
class CouponControllerIntegrationTest {

    @Autowired
    private CouponController couponController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private UserFacade userFacade;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        COUPON = couponRepository.save(new Coupon(1000L, 100L));
    }

    @Test
    @DisplayName("사용자를 생성한 후 사용자ID와 쿠폰ID를 입력받아 선착순 쿠폰 발급 진행한다.")
    void issueTest() throws Exception {

        // Arrange
        UserResult.Create userResult = userFacade.createUser(new UserCriteria.Create("추경현"));
        CouponRequest.Issue request = new CouponRequest.Issue(userResult.id(), COUPON.getId());

        // Act
        ResponseEntity<CouponResponse> response = couponController.issue(request);

        // Assert
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(userResult.id(), COUPON.getId());
        assertThat(issuedCoupon.getStatus()).isEqualTo(CouponStatus.ISSUED);

        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
            {
                "couponId": 1,
                "status": "ISSUED"
            }
            """;

        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
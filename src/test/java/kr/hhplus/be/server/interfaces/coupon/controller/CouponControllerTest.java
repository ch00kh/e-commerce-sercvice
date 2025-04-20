package kr.hhplus.be.server.interfaces.coupon.controller;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.dto.CouponCriteria;
import kr.hhplus.be.server.application.coupon.dto.CouponResult;
import kr.hhplus.be.server.domain.coupon.entity.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CouponController.class)
@DisplayName("[단위테스트] CouponController")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponFacade couponFacade;

    @Test
    @DisplayName("[성공] 선착순 쿠폰 발급")
    void issueTest() throws Exception {

        // Arrange
        String requestBody = """
            {
                "userId": 1,
                "couponId": 100
            }
            """;

        String responseBody = """
            {
                "couponId": 100,
                "status": "ISSUED"
            }
            """;

        when(couponFacade.firstComeFirstIssue(new CouponCriteria.Issue(1L, 100L)))
                .thenReturn(new CouponResult.Issued(1L, 1L, 100L, CouponStatus.ISSUED, LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/coupon/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }
}
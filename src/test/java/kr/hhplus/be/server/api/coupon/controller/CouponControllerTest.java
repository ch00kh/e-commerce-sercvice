package kr.hhplus.be.server.api.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.coupon.dto.CouponRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[성공] 선착순 쿠폰 발급")
    void issueTest() throws Exception {

        CouponRequest mockRequest = CouponRequest.builder()
                .couponId(1001L)
                .build();

        String requestJson = objectMapper.writeValueAsString(mockRequest);

        mockMvc.perform(post("/api/coupon/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(1001L))
                .andExpect(jsonPath("$.status").value("ISSUED"));
    }
}
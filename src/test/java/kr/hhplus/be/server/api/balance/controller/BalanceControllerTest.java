package kr.hhplus.be.server.api.balance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.balance.dto.BalanceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[성공] 잔액 충전")
    void chargeTest() throws Exception {

        BalanceRequest mockRequest = BalanceRequest.builder()
                .amount(10000L)
                .build();

        String requestJson = objectMapper.writeValueAsString(mockRequest);

        mockMvc.perform(post("/api/balance/{id}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L))
                .andExpect(jsonPath("$.amount").value(10000L));
    }

    @Test
    @DisplayName("[성공] 잔액 조회")
    void findTest() throws Exception {

        mockMvc.perform(get("/api/balance/{id}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L))
                .andExpect(jsonPath("$.amount").value(15000L));
    }

}
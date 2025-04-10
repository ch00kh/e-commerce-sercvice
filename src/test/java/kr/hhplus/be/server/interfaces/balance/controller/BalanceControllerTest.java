package kr.hhplus.be.server.interfaces.balance.controller;

import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.application.balance.dto.BalanceCriteria;
import kr.hhplus.be.server.application.balance.dto.BalanceResult;
import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@DisplayName("[단위테스트] BalanceController")
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BalanceFacade balanceFacade;

    private Long USER_ID;
    private BalanceCriteria.Charge CRITERIA_CHARGE;
    private BalanceCriteria.Find CRITERIA_FIND;
    private BalanceResult.UserBalance RESULT;
    private BalanceCommand.Charge COMMAND;
    private Balance BALANCE;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        CRITERIA_CHARGE = new BalanceCriteria.Charge(1L, 1000L);
        CRITERIA_FIND = new BalanceCriteria.Find(1L);
        RESULT = new BalanceResult.UserBalance(1L, 1L, 2000L);
        COMMAND = new BalanceCommand.Charge(1L, 1000L);
        BALANCE = new Balance(1L, 1L, 1000L);
    }

    @Nested
    @DisplayName("잔액 충전")
    class charge {

        @Test
        @DisplayName("[성공] 잔액 충전")
        void charge_ok() throws Exception {

            // Arrange
            String requestBody = """
            {
                "amount": 1000
            }
            """;

            String responseBody = """
            {
                "userId": 1,
                "amount": 2000
            }
            """;

            when(balanceFacade.charge(CRITERIA_CHARGE)).thenReturn(RESULT);

            // Act & Assert
            mockMvc.perform(post("/api/v1/balances/{userId}", USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody));
        }

        @Test
        @DisplayName("[실패] 잔액 충전 - 충전 금액이 음수(BAD_REQUEST)")
        void charge_BadRequest() throws Exception {

            // Arrange
            String requestBody = """
            {
                "amount": -1000
            }
            """;

            String responseBody = """
            {
                "code": 400,
                "message": "BAD_REQUEST"
            }
            """;

            // Act & Assert
            mockMvc.perform(post("/api/v1/balances/{id}", USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody));
        }
    }

    @Nested
    @DisplayName("잔액 조회")
    class findBalance {

        @Test
        @DisplayName("[성공] 잔액 조회")
        void findBalance_ok() throws Exception {

            // Arrange
            String responseBody = """
            {
                "userId": 1,
                "amount": 2000
            }
            """;

            when(balanceFacade.findBalance(CRITERIA_FIND)).thenReturn(RESULT);

            // Act & Assert
            mockMvc.perform(get("/api/v1/balances/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody));
        }

    }

}
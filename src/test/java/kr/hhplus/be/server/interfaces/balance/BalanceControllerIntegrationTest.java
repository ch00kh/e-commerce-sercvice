package kr.hhplus.be.server.interfaces.balance;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.application.user.UserFacade;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.global.exception.GlobalExceptionHandler;
import kr.hhplus.be.server.interfaces.balance.controller.BalanceController;
import kr.hhplus.be.server.interfaces.balance.dto.BalanceRequest;
import kr.hhplus.be.server.interfaces.balance.dto.BalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] BalanceController")
class BalanceControllerIntegrationTest {

    @Autowired
    private BalanceController balanceController;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    UserResult.Create USER;

    @BeforeEach
    void setUp() {
        USER = userFacade.createUser(new UserCriteria.Create("추경현"));
    }

    @Nested
    @DisplayName("잔액 충전")
    class charge {

        @Test
        @DisplayName("사용자ID와 충전금액을 입력받아 잔액 충전을한다.")
        void charge_ok() throws Exception {

            // Arrange
            BalanceRequest.Charge requestBody = new BalanceRequest.Charge(1000L);

            // Act
            ResponseEntity<BalanceResponse.UserBalance> response = balanceController.charge(USER.id(), requestBody);

            // Assert
            String actual = objectMapper.writeValueAsString(response.getBody());
            String responseBody = """
            {
                "userId": 1,
                "amount": 1000
            }
            """;

            assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }

        @Test
        @DisplayName("충전 금액은 음수일 수 없어 잔액 충전을 할 수 없다.")
        void charge_BadRequest() throws Exception {

            // Arrange
            BalanceRequest.Charge requestBody = new BalanceRequest.Charge(-1000L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> balanceController.charge(USER.id(), requestBody));

            // Assert
            ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(exception);
            String responseBody = """
            {
                "code": 400,
                "message": "INVALID_CHARGE_AMOUNT"
            }
            """;
            String actual = objectMapper.writeValueAsString(response.getBody());

            assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
            assertThat(response.getStatusCode().value()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("잔액 조회")
    class findBalance {

        @Test
        @DisplayName("사용자 ID로 잔액을 조회한다.")
        void findBalance_ok() throws Exception {

            // Arrange & Act
            ResponseEntity<BalanceResponse.UserBalance> response = balanceController.findBalance(USER.id());

            // Assert
            String actual = objectMapper.writeValueAsString(response.getBody());
            String responseBody = """
            {
                "userId": 1,
                "amount": 0
            }
            """;
            assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }

    }

}
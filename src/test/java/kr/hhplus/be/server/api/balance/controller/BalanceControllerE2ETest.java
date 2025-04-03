package kr.hhplus.be.server.api.balance.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("[성공] 잔액 충전")
    public void chargeTest() {

        String requestBody = """
                    {
                        "amount": 10000
                    }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/balance/{id}", 1001L)
            .then()
                .statusCode(200)
                .body("userId", equalTo(1001))
                .body("amount", equalTo(10000));
    }

    @Test
    @DisplayName("[성공] 잔액 조회")
    void findTest() {

        given()
            .when()
                .get("/api/balance/{id}", 1001L)
            .then()
                .statusCode(200)
                .body("userId", equalTo(1001))
                .body("amount", equalTo(15000));
    }
}

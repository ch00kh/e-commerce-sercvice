package kr.hhplus.be.server.api.payment.controller;

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
class PaymentControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("[성공] 결제 처리")
    public void processPaymentE2ESuccess() {
        String requestBody = """
                {
                    "userId": 1001,
                    "orderId": 10001
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/payment")
            .then()
                .statusCode(200)
                .body("orderId", equalTo(10001))
                .body("status", equalTo("COMPLETE"));
    }
}
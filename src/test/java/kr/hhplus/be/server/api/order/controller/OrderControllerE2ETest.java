package kr.hhplus.be.server.api.order.controller;

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
class OrderControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("[성공] 주문 생성")
    void order() {

        String requestBody = """
                {
                    "userId": 1001,
                    "productId": 1001,
                    "items": [
                        {
                            "product_option_id": 101,
                            "quantity": 2
                        }
                    ],
                    "couponId": 1001
                }
                """;

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/order")
            .then()
                .statusCode(200)
                .body("orderId", equalTo(10001))
                .body("userId", equalTo(1001))
                .body("productId", equalTo(10001))
                .body("status", equalTo("PENDING"))
                .body("totalAmount", equalTo(11000))
                .body("discountAmount", equalTo(1000))
                .body("paymentAmount", equalTo(10000));
    }
}
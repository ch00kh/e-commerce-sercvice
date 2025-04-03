package kr.hhplus.be.server.api.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                            "product_detail_id": 101,
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
                .body("totalAmount", equalTo("11000"))
                .body("discountAmount", equalTo("1000"))
                .body("paymentAmount", equalTo("10000"));
    }
}
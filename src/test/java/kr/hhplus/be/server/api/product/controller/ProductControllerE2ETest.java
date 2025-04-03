package kr.hhplus.be.server.api.product.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("[성공] 상품 목록 조회")
    void findAllTest() {

        given()
            .when()
                .get("/api/product") // 엔드포인트 경로에 맞게 수정하세요
            .then()
                .statusCode(200)
                .body("size()", equalTo(2)) // 목록 크기 검증

                .body("[0].productId", equalTo(1002))
                .body("[0].brand", equalTo("총각쓰떡"))
                .body("[0].name", equalTo("백설기"))
                .body("[0].options.size()", equalTo(2))
                .body("[0].options[0].productDetailId", equalTo(101))
                .body("[0].options[0].optionValue", equalTo("백설기/10개"))
                .body("[0].options[0].price", equalTo(5500))
                .body("[0].options[0].stock", equalTo(100))
                .body("[0].options[1].productDetailId", equalTo(102))
                .body("[0].options[1].optionValue", equalTo("우유설기/10개"))
                .body("[0].options[1].price", equalTo(5900))
                .body("[0].options[1].stock", equalTo(99))

                .body("[1].productId", equalTo(1002))
                .body("[1].brand", equalTo("총각쓰떡"))
                .body("[1].name", equalTo("백일떡"))
                .body("[1].options.size()", equalTo(1))
                .body("[1].options[0].productDetailId", equalTo(201))
                .body("[1].options[0].optionValue", equalTo("백일떡/10개"))
                .body("[1].options[0].price", equalTo(13700))
                .body("[1].options[0].stock", equalTo(200));
    }

    @Test
    @DisplayName("[성공] 상품 정보 조")
    void findTest() {

        given()
            .when()
                .get("/api/product/{id}", 1002)
            .then()
                .statusCode(200)

                .body("productId", equalTo(1002))
                .body("brand", equalTo("총각쓰떡"))
                .body("name", equalTo("백일떡"))
                .body("options.size()", equalTo(1))
                .body("options[0].productDetailId", equalTo(201))
                .body("options[0].optionValue", equalTo("백일떡/10개"))
                .body("options[0].price", equalTo(13700))
                .body("options[0].stock", equalTo(200));
    }

    @Test
    @DisplayName("[성공] 인기 판매 상품 조회")
    public void findBestProductsWithDefaultParamsE2E() {

        given()
            .when()
                .get("/api/product/best")
            .then()
                .statusCode(200)
                .body("size()", equalTo(3))

                .body("[0].productId", equalTo(1001))
                .body("[0].brand", equalTo("총각쓰떡"))
                .body("[0].name", equalTo("백설기"))
                .body("[0].totalOrders", equalTo(100))
                .body("[0].option.detailId", equalTo(101))
                .body("[0].option.optionValue", equalTo("백설기/10개"))
                .body("[0].option.price", equalTo(5500))
                .body("[0].option.stock", equalTo(100))

                .body("[1].productId", equalTo(1001))
                .body("[1].brand", equalTo("총각쓰떡"))
                .body("[1].name", equalTo("백설기"))
                .body("[1].totalOrders", equalTo(86))
                .body("[1].option.detailId", equalTo(102))
                .body("[1].option.optionValue", equalTo("우유설기/10개"))
                .body("[1].option.price", equalTo(5900))
                .body("[1].option.stock", equalTo(99))

                .body("[2].productId", equalTo(1001))
                .body("[2].brand", equalTo("총각쓰떡"))
                .body("[2].name", equalTo("백일떡"))
                .body("[2].totalOrders", equalTo(32))
                .body("[2].option.detailId", equalTo(201))
                .body("[2].option.optionValue", equalTo("백일떡/10개"))
                .body("[2].option.price", equalTo(13700))
                .body("[2].option.stock", equalTo(92));
    }
}
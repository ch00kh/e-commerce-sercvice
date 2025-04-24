package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.interfaces.product.controller.ProductController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@DisplayName("[단위테스트] ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProductFacade productFacade;

    private Long PRODUCT_ID1;
    private Long PRODUCT_ID2;

    private ProductResult.ProductAggregate PRODUCT1;
    private ProductResult.Option PRODUCT_OPTION1;
    private ProductResult.Option PRODUCT_OPTION2;
    
    private ProductResult.ProductAggregate PRODUCT2;
    private ProductResult.Option PRODUCT_OPTION3;

    @BeforeEach
    void setUp() {
        PRODUCT_ID1 = 1L;
        PRODUCT_OPTION1 = new ProductResult.Option(101L, "백설기/10개", 5500L, 100L);
        PRODUCT_OPTION2 = new ProductResult.Option(102L, "우유설기/10개", 5900L, 99L);
        PRODUCT1 = new ProductResult.ProductAggregate(PRODUCT_ID1, "총각쓰떡", "백설기", List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));

        PRODUCT_ID2 = 2L;
        PRODUCT_OPTION3 = new ProductResult.Option(111L, "백일떡/10개", 13700L, 50L);
        PRODUCT2 = new ProductResult.ProductAggregate(PRODUCT_ID2, "총각쓰떡", "백일떡", List.of(PRODUCT_OPTION3));
    }

    @Test
    @DisplayName("[성공] 상품 목록 조회")
    void findProductAll() throws Exception {

        // Arrange
        when(productFacade.findAll()).thenReturn(new ProductResult.ProductList(List.of(PRODUCT1, PRODUCT2)));

        String responseBody = """
                {
                  "products": [
                    {
                      "productId": 1,
                      "brand": "총각쓰떡",
                      "name": "백설기",
                      "options": [
                        {
                          "optionId": 101,
                          "optionValue": "백설기/10개",
                          "price": 5500,
                          "stock": 100
                        },
                        {
                          "optionId": 102,
                          "optionValue": "우유설기/10개",
                          "price": 5900,
                          "stock": 99
                        }
                      ]
                    },
                    {
                      "productId": 2,
                      "brand": "총각쓰떡",
                      "name": "백일떡",
                      "options": [
                        {
                          "optionId": 111,
                          "optionValue": "백일떡/10개",
                          "price": 13700,
                          "stock": 50
                        }
                      ]
                    }
                  ]
                }
                """;

        // Act & Assert
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    @DisplayName("[성공] 상품 정보 조회")
    void findProduct() throws Exception {

        // Arrange
        when(productFacade.findProduct(new ProductCriteria.Find(PRODUCT_ID1))).thenReturn(PRODUCT1);

        String responseBody = """
                {
                    "productId": 1,
                    "brand": "총각쓰떡",
                    "name": "백설기",
                    "options": [
                        {
                            "optionId": 101,
                            "optionValue": "백설기/10개",
                            "price": 5500,
                            "stock": 100
                        },
                        {
                            "optionId": 102,
                            "optionValue": "우유설기/10개",
                            "price": 5900,
                            "stock": 99
                        }
                    ]
                }
                """;

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", PRODUCT_ID1))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

}
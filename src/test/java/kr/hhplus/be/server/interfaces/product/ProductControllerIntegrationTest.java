package kr.hhplus.be.server.interfaces.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.interfaces.product.controller.ProductController;
import kr.hhplus.be.server.interfaces.product.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] ProductController")
class ProductControllerIntegrationTest {

    @Autowired
    private ProductController productController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private Product PRODUCT1;
    private ProductOption PRODUCT1_OPTION1;
    private ProductOption PRODUCT1_OPTION2;

    private Product PRODUCT2;
    private ProductOption PRODUCT2_OPTION1;
    private ProductOption PRODUCT2_OPTION2;

    private Product PRODUCT3;
    private ProductOption PRODUCT3_OPTION1;

    @BeforeEach
    void setUp() {
        PRODUCT1 = productRepository.save(new Product("맥도날드", "햄버거"));
        PRODUCT1_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "빅맥", 1000L, 100L));
        PRODUCT1_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "맥스파이시", 1000L, 100L));

        PRODUCT2 = productRepository.save(new Product("롯데리아", "햄버거"));
        PRODUCT2_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "데리버거", 1000L, 100L));
        PRODUCT2_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "핫스파이시", 1000L, 100L));

        PRODUCT3 = productRepository.save(new Product("맘스터치", "햄버거"));
        PRODUCT3_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT3.getId(), "싸이버거", 1000L, 100L));
    }

    @Test
    @DisplayName("전체 상품 목록을 조회한다.")
    void findProductAll() throws JsonProcessingException {

        // Act
        ResponseEntity<ProductResponse.ProductList> response = productController.findAll();

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
                {
                  "products": [
                    {
                      "productId": 1,
                      "brand": "맥도날드",
                      "name": "햄버거",
                      "options": [
                        {
                          "optionId": 1,
                          "optionValue": "빅맥",
                          "price": 1000,
                          "stock": 100
                        },
                        {
                          "optionId": 2,
                          "optionValue": "맥스파이시",
                          "price": 1000,
                          "stock": 100
                        }
                      ]
                    },
                    {
                      "productId": 2,
                      "brand": "롯데리아",
                      "name": "햄버거",
                      "options": [
                        {
                          "optionId": 3,
                          "optionValue": "데리버거",
                          "price": 1000,
                          "stock": 100
                        },
                        {
                          "optionId": 4,
                          "optionValue": "핫스파이시",
                          "price": 1000,
                          "stock": 100
                        }
                      ]
                    },
                    {
                      "productId": 3,
                      "brand": "맘스터치",
                      "name": "햄버거",
                      "options": [
                        {
                          "optionId": 5,
                          "optionValue": "싸이버거",
                          "price": 1000,
                          "stock": 100
                        }
                      ]
                    }
                  ]
                }
                """;
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    }

    @Test
    @DisplayName("상품ID를 입력받아 상품 정보를 조회한다.")
    void findProduct() throws JsonProcessingException {

        // Arrange & Act
        ResponseEntity<ProductResponse.ProductAggregate> response = productController.findProduct(1L);

        // Assert
        String actual = objectMapper.writeValueAsString(response.getBody());
        String responseBody = """
                {
                  "productId": 1,
                  "brand": "맥도날드",
                  "name": "햄버거",
                  "options": [
                    {
                      "optionId": 1,
                      "optionValue": "빅맥",
                      "price": 1000,
                      "stock": 100
                    },
                    {
                      "optionId": 2,
                      "optionValue": "맥스파이시",
                      "price": 1000,
                      "stock": 100
                    }
                  ]
                }
                """;

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(actual).isEqualToIgnoringWhitespace(responseBody);

    }

}
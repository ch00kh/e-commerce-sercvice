package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] ProductService")
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductOptionRepository productOptionRepository;

    @InjectMocks
    ProductService productService;

    private Long PRODUCT_ID1;
    private Product PRODUCT1;
    private ProductOption PRODUCT_OPTION1;
    private ProductOption PRODUCT_OPTION2;

    private Long PRODUCT_ID2;
    private Product PRODUCT2;
    private ProductOption PRODUCT_OPTION3;

    @BeforeEach
    void setUp() {
        PRODUCT_ID1 = 1L;
        PRODUCT1 = new Product(PRODUCT_ID1, "총각쓰떡", "백설기");
        PRODUCT_OPTION1 = new ProductOption(101L, "백설기/10개", 5500L, 100L);
        PRODUCT_OPTION2 = new ProductOption(102L, "우유설기/10개", 5900L, 99L);

        PRODUCT_ID2 = 2L;
        PRODUCT2 = new Product(PRODUCT_ID2, "총각쓰떡", "백일떡");
        PRODUCT_OPTION3 = new ProductOption(111L, "백일떡/10개", 13700L, 50L);
    }

    @Test
    @DisplayName("[성공] 상품 목록 조회")
    void findAll() {

        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(PRODUCT1, PRODUCT2));
        when(productOptionRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));
        when(productOptionRepository.findByProductId(PRODUCT_ID2)).thenReturn(List.of(PRODUCT_OPTION3));

        // Act
        ProductInfo.ProductList actualProducts = productService.findAll();

        // Assert
        verify(productRepository, times(1)).findAll();
        verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID1);
        verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID2);

        assertThat(actualProducts.products().get(0).productId()).isEqualTo(PRODUCT_ID1);
        assertThat(actualProducts.products().get(0).brand()).isEqualTo("총각쓰떡");
        assertThat(actualProducts.products().get(0).name()).isEqualTo("백설기");
        assertThat(actualProducts.products().get(0).options().get(0).getId()).isEqualTo(101);
        assertThat(actualProducts.products().get(0).options().get(0).getOptionValue()).isEqualTo("백설기/10개");
        assertThat(actualProducts.products().get(0).options().get(0).getPrice()).isEqualTo(5500);
        assertThat(actualProducts.products().get(0).options().get(0).getStock()).isEqualTo(100);
        assertThat(actualProducts.products().get(0).options().get(1).getId()).isEqualTo(102);
        assertThat(actualProducts.products().get(0).options().get(1).getOptionValue()).isEqualTo("우유설기/10개");
        assertThat(actualProducts.products().get(0).options().get(1).getPrice()).isEqualTo(5900);
        assertThat(actualProducts.products().get(0).options().get(1).getStock()).isEqualTo(99);

        assertThat(actualProducts.products().get(1).productId()).isEqualTo(PRODUCT_ID2);
        assertThat(actualProducts.products().get(1).brand()).isEqualTo("총각쓰떡");
        assertThat(actualProducts.products().get(1).name()).isEqualTo("백일떡");
        assertThat(actualProducts.products().get(1).options().get(0).getId()).isEqualTo(111);
        assertThat(actualProducts.products().get(1).options().get(0).getOptionValue()).isEqualTo("백일떡/10개");
        assertThat(actualProducts.products().get(1).options().get(0).getPrice()).isEqualTo(13700);
        assertThat(actualProducts.products().get(1).options().get(0).getStock()).isEqualTo(50);
    }


    @Nested
    @DisplayName("상품 정보 조회")
    class findProduct {

        @Test
        @DisplayName("[성공] 상품 정보 조회")
        void findProduct_ok() {

            // Arrange
            when(productRepository.findById(PRODUCT_ID1)).thenReturn(Optional.of(PRODUCT1));
            when(productOptionRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));

            // Act
            ProductInfo.ProductAggregate actualProductInfo = productService.findProduct(new ProductCommand.Find(PRODUCT_ID1));

            // Assert
            verify(productRepository, times(1)).findById(PRODUCT_ID1);
            verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID1);

            assertThat(actualProductInfo.productId()).isEqualTo(PRODUCT_ID1);
            assertThat(actualProductInfo.brand()).isEqualTo("총각쓰떡");
            assertThat(actualProductInfo.name()).isEqualTo("백설기");
            assertThat(actualProductInfo.options().get(0).getId()).isEqualTo(101L);
            assertThat(actualProductInfo.options().get(0).getOptionValue()).isEqualTo("백설기/10개");
            assertThat(actualProductInfo.options().get(0).getPrice()).isEqualTo(5500L);
            assertThat(actualProductInfo.options().get(0).getStock()).isEqualTo(100L);
            assertThat(actualProductInfo.options().get(1).getId()).isEqualTo(102L);
            assertThat(actualProductInfo.options().get(1).getOptionValue()).isEqualTo("우유설기/10개");
            assertThat(actualProductInfo.options().get(1).getPrice()).isEqualTo(5900L);
            assertThat(actualProductInfo.options().get(1).getStock()).isEqualTo(99L);
        }

        @Test
        @DisplayName("[실패] 상품 정보 조회 -> 상품 없음 예외(NOT_FOUND)")
        void findProduct_NotFound() {

            // Arrange
            when(productRepository.findById(PRODUCT_ID1)).thenReturn(Optional.empty());

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> productService.findProduct(new ProductCommand.Find(PRODUCT_ID1)));

            // Assert
            verify(productRepository, times(1)).findById(PRODUCT_ID1);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}
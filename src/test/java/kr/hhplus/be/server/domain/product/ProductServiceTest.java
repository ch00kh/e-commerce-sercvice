package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
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
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

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
        PRODUCT_OPTION1 = new ProductOption(PRODUCT_ID1, "백설기/10개",5500L, 100);
        PRODUCT_OPTION2 = new ProductOption(PRODUCT_ID1, "우유설기/10개",5900L, 99);

        PRODUCT_ID2 = 2L;
        PRODUCT2 = new Product(PRODUCT_ID2, "총각쓰떡", "백일떡");
        PRODUCT_OPTION3 = new ProductOption(PRODUCT_ID2, "백일떡/10개",13700L, 50);
    }

    @Test
    @DisplayName("[성공] 상품 목록 조회")
    void findAll() {

        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(PRODUCT1, PRODUCT2));
        when(productOptionRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));
        when(productOptionRepository.findByProductId(PRODUCT_ID2)).thenReturn(List.of(PRODUCT_OPTION3));

        // Act
        ProductInfo.ProductList actualInfo = productService.findAll();

        // Assert
        verify(productRepository, times(1)).findAll();
        verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID1);
        verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID2);

        assertThat(actualInfo.products().get(0).productId()).isEqualTo(PRODUCT_ID1);
        assertThat(actualInfo.products().get(0).brand()).isEqualTo("총각쓰떡");
        assertThat(actualInfo.products().get(0).name()).isEqualTo("백설기");
        assertThat(actualInfo.products().get(0).options().get(0).getOptionValue()).isEqualTo("백설기/10개");
        assertThat(actualInfo.products().get(0).options().get(0).getPrice()).isEqualTo(5500);
        assertThat(actualInfo.products().get(0).options().get(0).getStock()).isEqualTo(100);
        assertThat(actualInfo.products().get(0).options().get(1).getId()).isEqualTo(102);
        assertThat(actualInfo.products().get(0).options().get(1).getOptionValue()).isEqualTo("우유설기/10개");
        assertThat(actualInfo.products().get(0).options().get(1).getPrice()).isEqualTo(5900);
        assertThat(actualInfo.products().get(0).options().get(1).getStock()).isEqualTo(99);

        assertThat(actualInfo.products().get(1).productId()).isEqualTo(PRODUCT_ID2);
        assertThat(actualInfo.products().get(1).brand()).isEqualTo("총각쓰떡");
        assertThat(actualInfo.products().get(1).name()).isEqualTo("백일떡");
        assertThat(actualInfo.products().get(1).options().get(0).getOptionValue()).isEqualTo("백일떡/10개");
        assertThat(actualInfo.products().get(1).options().get(0).getPrice()).isEqualTo(13700);
        assertThat(actualInfo.products().get(1).options().get(0).getStock()).isEqualTo(50);
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
            ProductInfo.ProductAggregate actualInfo = productService.findProduct(new ProductCommand.Find(PRODUCT_ID1));

            // Assert
            verify(productRepository, times(1)).findById(PRODUCT_ID1);
            verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID1);

            assertThat(actualInfo.productId()).isEqualTo(PRODUCT_ID1);
            assertThat(actualInfo.brand()).isEqualTo("총각쓰떡");
            assertThat(actualInfo.name()).isEqualTo("백설기");
            assertThat(actualInfo.options().get(0).getOptionValue()).isEqualTo("백설기/10개");
            assertThat(actualInfo.options().get(0).getPrice()).isEqualTo(5500L);
            assertThat(actualInfo.options().get(0).getStock()).isEqualTo(100L);
            assertThat(actualInfo.options().get(1).getId()).isEqualTo(102L);
            assertThat(actualInfo.options().get(1).getOptionValue()).isEqualTo("우유설기/10개");
            assertThat(actualInfo.options().get(1).getPrice()).isEqualTo(5900L);
            assertThat(actualInfo.options().get(1).getStock()).isEqualTo(99L);
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

    @Nested
    @DisplayName("재고 차감")
    class reduceStock {

        @Test
        @DisplayName("[성공] 모든 상품 재고 여유 -> CheckStock isEnough 값 검증 true")
        void reduceStock_ok() {

            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 10),
                    new OrderCommand.OrderItem(102L, 5900L, 9)
            );

            when(productOptionRepository.findById(101L)).thenReturn(Optional.of(PRODUCT_OPTION1));
            when(productOptionRepository.findById(102L)).thenReturn(Optional.of(PRODUCT_OPTION2));

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION1.getId());
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION2.getId());

            assertThat(actualInfo.checkStocks().size()).isEqualTo(2);

            assertThat(actualInfo.checkStocks().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.checkStocks().get(0).remainingQuantity()).isEqualTo(90);
            assertThat(actualInfo.checkStocks().get(0).requestQuantity()).isEqualTo(10);
            assertThat(actualInfo.checkStocks().get(0).isEnough()).isEqualTo(true);

            assertThat(actualInfo.checkStocks().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.checkStocks().get(1).remainingQuantity()).isEqualTo(90);
            assertThat(actualInfo.checkStocks().get(1).requestQuantity()).isEqualTo(9);
            assertThat(actualInfo.checkStocks().get(1).isEnough()).isEqualTo(true);
        }

        @Test
        @DisplayName("[성공/실패] 모든 상품 재고 여유(경계값) -> CheckStock isEnough 값 검증 true")
        void reduceStock_ok_BoundaryCheck() {
            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 99),
                    new OrderCommand.OrderItem(102L, 5900L, 99)
            );

            when(productOptionRepository.findById(101L)).thenReturn(Optional.of(PRODUCT_OPTION1));
            when(productOptionRepository.findById(102L)).thenReturn(Optional.of(PRODUCT_OPTION2));

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION1.getId());
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION2.getId());

            assertThat(actualInfo.checkStocks().size()).isEqualTo(2);

            assertThat(actualInfo.checkStocks().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.checkStocks().get(0).remainingQuantity()).isEqualTo(1);
            assertThat(actualInfo.checkStocks().get(0).requestQuantity()).isEqualTo(99);
            assertThat(actualInfo.checkStocks().get(0).isEnough()).isEqualTo(true);

            assertThat(actualInfo.checkStocks().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.checkStocks().get(1).remainingQuantity()).isEqualTo(0);
            assertThat(actualInfo.checkStocks().get(1).requestQuantity()).isEqualTo(99);
            assertThat(actualInfo.checkStocks().get(1).isEnough()).isEqualTo(true);
        }

        @Test
        @DisplayName("[실패] 일부 상품 재고 부족 -> 예외는 아니지만, CheckStock 검증")
        void reduceStock_ok_anyStockIsNotEnough() {
            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 101),
                    new OrderCommand.OrderItem(102L, 5900L, 100)
            );

            when(productOptionRepository.findById(101L)).thenReturn(Optional.of(PRODUCT_OPTION1));
            when(productOptionRepository.findById(102L)).thenReturn(Optional.of(PRODUCT_OPTION2));

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION1.getId());
            verify(productOptionRepository, times(1)).findById(PRODUCT_OPTION2.getId());

            assertThat(actualInfo.checkStocks().size()).isEqualTo(2);

            assertThat(actualInfo.checkStocks().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.checkStocks().get(0).remainingQuantity()).isEqualTo(100);
            assertThat(actualInfo.checkStocks().get(0).requestQuantity()).isEqualTo(101);
            assertThat(actualInfo.checkStocks().get(0).isEnough()).isEqualTo(false);

            assertThat(actualInfo.checkStocks().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.checkStocks().get(1).remainingQuantity()).isEqualTo(99);
            assertThat(actualInfo.checkStocks().get(1).requestQuantity()).isEqualTo(100);
            assertThat(actualInfo.checkStocks().get(1).isEnough()).isEqualTo(false);
        }
    }
}
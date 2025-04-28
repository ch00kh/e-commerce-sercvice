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
        PRODUCT_OPTION1 = new ProductOption(PRODUCT_ID1, "백설기/10개",5500L, 100L);
        PRODUCT_OPTION2 = new ProductOption(PRODUCT_ID1, "우유설기/10개",5900L, 99L);

        PRODUCT_ID2 = 2L;
        PRODUCT2 = new Product(PRODUCT_ID2, "총각쓰떡", "백일떡");
        PRODUCT_OPTION3 = new ProductOption(PRODUCT_ID2, "백일떡/10개",13700L, 50L);
    }

    @Test
    @DisplayName("상품 목록을 조회 한다.")
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
        assertThat(actualInfo.products().get(0).options().get(0).optionValue()).isEqualTo("백설기/10개");
        assertThat(actualInfo.products().get(0).options().get(0).price()).isEqualTo(5500);
        assertThat(actualInfo.products().get(0).options().get(0).stock()).isEqualTo(100);
        assertThat(actualInfo.products().get(0).options().get(1).optionValue()).isEqualTo("우유설기/10개");
        assertThat(actualInfo.products().get(0).options().get(1).price()).isEqualTo(5900);
        assertThat(actualInfo.products().get(0).options().get(1).stock()).isEqualTo(99);

        assertThat(actualInfo.products().get(1).productId()).isEqualTo(PRODUCT_ID2);
        assertThat(actualInfo.products().get(1).brand()).isEqualTo("총각쓰떡");
        assertThat(actualInfo.products().get(1).name()).isEqualTo("백일떡");
        assertThat(actualInfo.products().get(1).options().get(0).optionValue()).isEqualTo("백일떡/10개");
        assertThat(actualInfo.products().get(1).options().get(0).price()).isEqualTo(13700);
        assertThat(actualInfo.products().get(1).options().get(0).stock()).isEqualTo(50);
    }


    @Nested
    @DisplayName("상품 정보 조회")
    class findProduct {

        @Test
        @DisplayName("상품ID로 상품 정보를 조회한다.")
        void findProduct_ok() {

            // Arrange
            when(productRepository.findById(PRODUCT_ID1)).thenReturn(PRODUCT1);
            when(productOptionRepository.findByProductId(PRODUCT_ID1)).thenReturn(List.of(PRODUCT_OPTION1, PRODUCT_OPTION2));

            // Act
            ProductInfo.ProductAggregate actualInfo = productService.findProduct(new ProductCommand.Find(PRODUCT_ID1));

            // Assert
            verify(productRepository, times(1)).findById(PRODUCT_ID1);
            verify(productOptionRepository, times(1)).findByProductId(PRODUCT_ID1);

            assertThat(actualInfo.productId()).isEqualTo(PRODUCT_ID1);
            assertThat(actualInfo.brand()).isEqualTo("총각쓰떡");
            assertThat(actualInfo.name()).isEqualTo("백설기");
            assertThat(actualInfo.options().get(0).optionValue()).isEqualTo("백설기/10개");
            assertThat(actualInfo.options().get(0).price()).isEqualTo(5500L);
            assertThat(actualInfo.options().get(0).stock()).isEqualTo(100L);
            assertThat(actualInfo.options().get(1).optionValue()).isEqualTo("우유설기/10개");
            assertThat(actualInfo.options().get(1).price()).isEqualTo(5900L);
            assertThat(actualInfo.options().get(1).stock()).isEqualTo(99L);
        }

        @Test
        @DisplayName("상품ID가 없어 상품 정보를 조회할 수 없다.")
        void findProduct_NotFound() {

            // Arrange
            when(productRepository.findById(PRODUCT_ID1)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

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
        @DisplayName("모든 상품의 재고가 여유가 있어 재고 차감에 성공한다.")
        void reduceStock_ok() {

            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 10L),
                    new OrderCommand.OrderItem(102L, 5900L, 9L)
            );

            when(productOptionRepository.findByIdWithPessimisticLock(101L)).thenReturn(PRODUCT_OPTION1);
            when(productOptionRepository.findByIdWithPessimisticLock(102L)).thenReturn(PRODUCT_OPTION2);

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(101L);
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(102L);

            assertThat(actualInfo.optionDetails().size()).isEqualTo(2);

            assertThat(actualInfo.optionDetails().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.optionDetails().get(0).remainingQuantity()).isEqualTo(90);
            assertThat(actualInfo.optionDetails().get(0).requestQuantity()).isEqualTo(10);
            assertThat(actualInfo.optionDetails().get(0).canPurchase()).isEqualTo(true);

            assertThat(actualInfo.optionDetails().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.optionDetails().get(1).remainingQuantity()).isEqualTo(90);
            assertThat(actualInfo.optionDetails().get(1).requestQuantity()).isEqualTo(9);
            assertThat(actualInfo.optionDetails().get(1).canPurchase()).isEqualTo(true);
        }

        @Test
        @DisplayName("주문 상품과 상품의 재고가 동일하여 상품 재고 차감에 성공한다.")
        void reduceStock_ok_BoundaryCheck() {

            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 99L),
                    new OrderCommand.OrderItem(102L, 5900L, 99L)
            );

            when(productOptionRepository.findByIdWithPessimisticLock(101L)).thenReturn(PRODUCT_OPTION1);
            when(productOptionRepository.findByIdWithPessimisticLock(102L)).thenReturn(PRODUCT_OPTION2);

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(101L);
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(102L);

            assertThat(actualInfo.optionDetails().size()).isEqualTo(2);

            assertThat(actualInfo.optionDetails().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.optionDetails().get(0).remainingQuantity()).isEqualTo(1);
            assertThat(actualInfo.optionDetails().get(0).requestQuantity()).isEqualTo(99);
            assertThat(actualInfo.optionDetails().get(0).canPurchase()).isEqualTo(true);

            assertThat(actualInfo.optionDetails().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.optionDetails().get(1).remainingQuantity()).isEqualTo(0);
            assertThat(actualInfo.optionDetails().get(1).requestQuantity()).isEqualTo(99);
            assertThat(actualInfo.optionDetails().get(1).canPurchase()).isEqualTo(true);
        }

        @Test
        @DisplayName("일부 상품은 재고 부족하여 재고 차감을 하지 않는다.")
        void reduceStock_ok_anyStockIsNotEnough() {

            // Arrange
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(101L, 5500L, 101L),
                    new OrderCommand.OrderItem(102L, 5900L, 100L)
            );

            when(productOptionRepository.findByIdWithPessimisticLock(101L)).thenReturn(PRODUCT_OPTION1);
            when(productOptionRepository.findByIdWithPessimisticLock(102L)).thenReturn(PRODUCT_OPTION2);

            // Act
            ProductInfo.Order actualInfo = productService.reduceStock(orderItems);

            // Assert
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(101L);
            verify(productOptionRepository, times(1)).findByIdWithPessimisticLock(102L);

            assertThat(actualInfo.optionDetails().size()).isEqualTo(2);

            assertThat(actualInfo.optionDetails().get(0).optionId()).isEqualTo(PRODUCT_OPTION1.getId());
            assertThat(actualInfo.optionDetails().get(0).remainingQuantity()).isEqualTo(100);
            assertThat(actualInfo.optionDetails().get(0).requestQuantity()).isEqualTo(101);
            assertThat(actualInfo.optionDetails().get(0).canPurchase()).isEqualTo(false);

            assertThat(actualInfo.optionDetails().get(1).optionId()).isEqualTo(PRODUCT_OPTION2.getId());
            assertThat(actualInfo.optionDetails().get(1).remainingQuantity()).isEqualTo(99);
            assertThat(actualInfo.optionDetails().get(1).requestQuantity()).isEqualTo(100);
            assertThat(actualInfo.optionDetails().get(1).canPurchase()).isEqualTo(false);
        }
    }
}
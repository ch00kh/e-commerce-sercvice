package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import kr.hhplus.be.server.surpport.cleaner.DatabaseClearExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] ProductService")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderService orderService;

    private Product PRODUCT1;
    private ProductOption PRODUCT1_OPTION1;
    private ProductOption PRODUCT1_OPTION2;

    private Product PRODUCT2;
    private ProductOption PRODUCT2_OPTION1;
    private ProductOption PRODUCT2_OPTION2;
    private ProductOption PRODUCT2_OPTION3;

    @BeforeEach
    void setUp() {
        PRODUCT1 = productRepository.save(new Product("양반", "김"));
        PRODUCT1_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "들기름 김", 1000L, 1000L));
        PRODUCT1_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "참기름 김", 1250L, 500L));

        PRODUCT2 = productRepository.save(new Product("엽기떡볶이", "떡볶이"));
        PRODUCT2_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "초보 맛", 12000L, 1000L));
        PRODUCT2_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "중간 맛", 12500L, 500L));
        PRODUCT2_OPTION3 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "죽을 맛", 13000L, 150L));
    }

    @Nested
    @DisplayName("상품 조회")
    class findProduct {

        @Test
        @DisplayName("상품 목록을 조회 한다.")
        void findAll() {

            // Act
            ProductInfo.ProductList result = productService.findAll();

            // Assert
            assertThat(result.products()).hasSize(2);

            ProductInfo.ProductAggregate actual1 = result.products().get(0);
            assertThat(actual1.productId()).isEqualTo(PRODUCT1.getId());
            assertThat(actual1.brand()).isEqualTo("양반");
            assertThat(actual1.name()).isEqualTo("김");

            assertThat(actual1.options()).hasSize(2);
            assertThat(actual1.options().get(0).optionId()).isEqualTo(PRODUCT1_OPTION1.getId());
            assertThat(actual1.options().get(0).optionValue()).isEqualTo("들기름 김");
            assertThat(actual1.options().get(0).price()).isEqualTo(1000L);
            assertThat(actual1.options().get(0).stock()).isEqualTo(1000L);

            assertThat(actual1.options().get(1).optionId()).isEqualTo(PRODUCT1_OPTION2.getId());
            assertThat(actual1.options().get(1).optionValue()).isEqualTo("참기름 김");
            assertThat(actual1.options().get(1).price()).isEqualTo(1250L);
            assertThat(actual1.options().get(1).stock()).isEqualTo(500L);

            ProductInfo.ProductAggregate actual2 = result.products().get(1);
            assertThat(actual2.productId()).isEqualTo(PRODUCT2.getId());
            assertThat(actual2.brand()).isEqualTo("엽기떡볶이");
            assertThat(actual2.name()).isEqualTo("떡볶이");

            assertThat(actual2.options()).hasSize(3);
            assertThat(actual2.options().get(0).optionId()).isEqualTo(PRODUCT2_OPTION1.getId());
            assertThat(actual2.options().get(0).optionValue()).isEqualTo("초보 맛");
            assertThat(actual2.options().get(0).price()).isEqualTo(12000L);
            assertThat(actual2.options().get(0).stock()).isEqualTo(1000L);

            assertThat(actual2.options().get(1).optionId()).isEqualTo(PRODUCT2_OPTION2.getId());
            assertThat(actual2.options().get(1).optionValue()).isEqualTo("중간 맛");
            assertThat(actual2.options().get(1).price()).isEqualTo(12500L);
            assertThat(actual2.options().get(1).stock()).isEqualTo(500L);

            assertThat(actual2.options().get(2).optionId()).isEqualTo(PRODUCT2_OPTION3.getId());
            assertThat(actual2.options().get(2).optionValue()).isEqualTo("죽을 맛");
            assertThat(actual2.options().get(2).price()).isEqualTo(13000L);
            assertThat(actual2.options().get(2).stock()).isEqualTo(150L);
        }

        @Test
        @DisplayName("상품ID로 상품 정보를 조회한다.")
        void findProduct_ok() {

            //Arrange
            ProductCommand.Find command = new ProductCommand.Find(PRODUCT1.getId());

            // Act
            ProductInfo.ProductAggregate actual = productService.findProduct(command);

            // Assert
            assertThat(actual.productId()).isEqualTo(PRODUCT1.getId());
            assertThat(actual.brand()).isEqualTo("양반");
            assertThat(actual.name()).isEqualTo("김");

            assertThat(actual.options()).hasSize(2);
            assertThat(actual.options().get(0).optionId()).isEqualTo(PRODUCT1_OPTION1.getId());
            assertThat(actual.options().get(0).optionValue()).isEqualTo("들기름 김");
            assertThat(actual.options().get(0).price()).isEqualTo(1000L);
            assertThat(actual.options().get(0).stock()).isEqualTo(1000L);

            assertThat(actual.options().get(1).optionId()).isEqualTo(PRODUCT1_OPTION2.getId());
            assertThat(actual.options().get(1).optionValue()).isEqualTo("참기름 김");
            assertThat(actual.options().get(1).price()).isEqualTo(1250L);
            assertThat(actual.options().get(1).stock()).isEqualTo(500L);
        }

        @Test
        @DisplayName("상품ID가 없어 상품 정보를 조회할 수 없다.")
        void findProduct_notFound() {

            //Arrange
            ProductCommand.Find command = new ProductCommand.Find(999L);

            // Act
            GlobalException exception = assertThrows(GlobalException.class, () -> productService.findProduct(command));

            // Assert
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }

    }
    
    @Nested
    @DisplayName("재고 차감")
    class reduceStock {

        @Test
        @DisplayName("재고가 충분 차감(OPTION1)하고, 부족하면 차감(OPTION2)하지 않는다.")
        @Transactional  // 트랜잭션 추가
        void reduceStockTest() {

            // Arrange - 먼저 주문을 생성
            List<OrderCommand.OrderItem> orderItems = List.of(
                    new OrderCommand.OrderItem(PRODUCT1_OPTION1.getId(), 1000L, 1000L),
                    new OrderCommand.OrderItem(PRODUCT1_OPTION2.getId(), 1250L, 600L)
            );
            
            OrderInfo.Create order = orderService.createOrder(new OrderCommand.Create(1L, null, orderItems));

            // Act - 재고 차감 직접 호출
            ProductInfo.Order actual = productService.reduceStock(new OrderCommand.Reduce(order.orderId(), orderItems));

            // Assert
            assertThat(actual.optionDetails()).hasSize(2);
            assertThat(actual.optionDetails().get(0).canPurchase()).isTrue();
            assertThat(actual.optionDetails().get(0).remainingQuantity()).isEqualTo(0);
            assertThat(actual.optionDetails().get(0).requestQuantity()).isEqualTo(1000);

            assertThat(actual.optionDetails().get(1).canPurchase()).isFalse();
            assertThat(actual.optionDetails().get(1).remainingQuantity()).isEqualTo(500L);
            assertThat(actual.optionDetails().get(1).requestQuantity()).isEqualTo(600L);

            ProductOption option1 = productOptionRepository.findById(PRODUCT1_OPTION1.getId());
            assertThat(option1.getStock()).isEqualTo(0);

            ProductOption option2 = productOptionRepository.findById(PRODUCT1_OPTION2.getId());
            assertThat(option2.getStock()).isEqualTo(500);
        }

    }

}

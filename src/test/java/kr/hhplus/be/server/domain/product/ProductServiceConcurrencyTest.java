package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] ProductService")
class ProductServiceConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    private Product PRODUCT;
    private ProductOption PRODUCT_OPTION;


    @BeforeEach
    void setUp() {
        PRODUCT = productRepository.save(new Product("양반", "김"));
        PRODUCT_OPTION = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "들기름 김", 1000L, 100L));
    }

    @Test
    @DisplayName("주문 중 재고 차감 시 모든 요청은 성공한다.")
    void reduceStockConcurrencyTest() throws InterruptedException {

        // Arrange
        int threadCount = 100;
        int threadPool = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    List<OrderCommand.OrderItem> command = List.of(new OrderCommand.OrderItem(PRODUCT_OPTION.getId(), 1000L, 1L));
                    ProductInfo.Order productInfo = productService.reduceStock(new OrderCommand.OrderItemList(command));

                    if (productInfo.optionDetails().get(0).canPurchase()) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error reducing stock: {}", e.getMessage());
                } finally {
                    taskLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        taskLatch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        ProductOption updatedOption = productOptionRepository.findById(PRODUCT_OPTION.getId());
        assertThat(updatedOption.getStock()).isEqualTo(100L - successCount.get());
    }

}

package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[동시성 테스트] OrderService")
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderService orderService;

    private OrderCommand.OrderItem ORDER_ITEM1;
    private List<OrderCommand.OrderItem> ORDER_ITEMS;

    private Product PRODUCT;
    private ProductOption PRODUCT_OPTION;

    @BeforeEach
    void setup() {
        orderItemRepository.deleteAll();
        productRepository.deleteAll();
        productOptionRepository.deleteAll();

        PRODUCT = productRepository.save(new Product("양반", "김"));
        PRODUCT_OPTION = productOptionRepository.save(new ProductOption(PRODUCT.getId(), "들기름 김", 1000L, 100L));

        ORDER_ITEM1 = new OrderCommand.OrderItem(PRODUCT_OPTION.getId(), 10000L, 1L);
        ORDER_ITEMS = List.of(ORDER_ITEM1);
    }

    @Test
    @DisplayName("상품 주문")
    void concurrentOrderCreation() throws InterruptedException {

        // Arrange
        int threadCount =1000;
        int threadPool = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Act
        List<Long> orderIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final long userId = i;
            executorService.submit(() -> {
                try {
                    OrderCommand.OrderItem orderItem = new OrderCommand.OrderItem(PRODUCT_OPTION.getId(), PRODUCT_OPTION.getPrice(), 2L);
                    OrderCommand.Create command = new OrderCommand.Create(userId, List.of(orderItem));

                    OrderInfo.Create order = orderService.createOrder(command);
                    orderIds.add(order.orderId());

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("Error order: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount.get(), failureCount.get());
        List<OrderItem> actual = orderItemRepository.findByProductOptionId(PRODUCT_OPTION.getId());
        assertThat(actual).hasSize(threadCount);
    }
}
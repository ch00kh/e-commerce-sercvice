package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.application.user.UserFacade;
import kr.hhplus.be.server.application.user.dto.UserCriteria;
import kr.hhplus.be.server.application.user.dto.UserResult;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.infra.persistence.order.OrderItemJpaRepository;
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
@DisplayName("[동시성 테스트] OrderFacadeTest")
class OrderFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderItemJpaRepository orderItemRepository;

    private UserResult.Create USER;
    private Product PRODUCT1;
    private ProductOption PRODUCT_OPTION1;
    private ProductOption PRODUCT_OPTION2;
    private ProductOption PRODUCT_OPTION3;
    private Product PRODUCT2;
    private ProductOption PRODUCT_OPTION4;
    private Long INITIAL_STOCK;

    @BeforeEach
    void setUp() {
        USER = userFacade.createUser(new UserCriteria.Create("추경현"));
        INITIAL_STOCK = 1000L;

        PRODUCT1 = productRepository.save(new Product("양반", "김"));
        PRODUCT_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "들기름 김", 1000L, INITIAL_STOCK));
        PRODUCT_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "참기름 김", 1000L, INITIAL_STOCK));
        PRODUCT_OPTION3 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "잘생 김", 1000L, INITIAL_STOCK));

        PRODUCT2 = productRepository.save(new Product("천민", "김"));
        PRODUCT_OPTION4 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "못생 김", 1000L, INITIAL_STOCK));
    }

    @Test
    @DisplayName("동시에 여러 건 주문 시 요청한 수에 맞는 재고를 차감한다.")
    void order() throws InterruptedException {

        // Arrange
        List<OrderCriteria.OrderItem> cart1 = List.of(
                new OrderCriteria.OrderItem(PRODUCT_OPTION1.getId(), 1L),
                new OrderCriteria.OrderItem(PRODUCT_OPTION2.getId(), 1L)
        );
        List<OrderCriteria.OrderItem> cart2 = List.of(
                new OrderCriteria.OrderItem(PRODUCT_OPTION2.getId(), 1L),
                new OrderCriteria.OrderItem(PRODUCT_OPTION3.getId(), 1L)
        );
        List<OrderCriteria.OrderItem> cart3 = List.of(
                new OrderCriteria.OrderItem(PRODUCT_OPTION1.getId(), 1L),
                new OrderCriteria.OrderItem(PRODUCT_OPTION3.getId(), 1L)
        );
        List<OrderCriteria.OrderItem> cart4 = List.of(
                new OrderCriteria.OrderItem(PRODUCT_OPTION4.getId(), 1L)
        );

        OrderCriteria.Create criteria1 = new OrderCriteria.Create(USER.id(), PRODUCT1.getId(), cart1, null);
        OrderCriteria.Create criteria2 = new OrderCriteria.Create(USER.id(), PRODUCT1.getId(), cart2, null);
        OrderCriteria.Create criteria3 = new OrderCriteria.Create(USER.id(), PRODUCT1.getId(), cart3, null);
        OrderCriteria.Create criteria4 = new OrderCriteria.Create(USER.id(), PRODUCT2.getId(), cart4, null);

        // Act
        int threadCount = INITIAL_STOCK.intValue();
        int threadPool = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadPool);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch taskLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount1 = new AtomicInteger(0);
        AtomicInteger successCount2 = new AtomicInteger(0);
        AtomicInteger successCount3 = new AtomicInteger(0);
        AtomicInteger successCount4 = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    startLatch.await();

                    if (finalI % 4 == 0) {
                        orderFacade.order(criteria1);
                        successCount1.incrementAndGet();
                    }
                    if (finalI % 4 == 1) {
                        orderFacade.order(criteria2);
                        successCount2.incrementAndGet();
                    }
                    if (finalI % 4 == 2) {
                        orderFacade.order(criteria3);
                        successCount3.incrementAndGet();
                    }
                    if (finalI % 4 == 3) {
                        orderFacade.order(criteria4);
                        successCount4.incrementAndGet();
                    }

                    successCount.incrementAndGet();

                } catch (Exception e) {
                    log.error("주문 처리 실패: {}", e.getMessage());
                    failureCount.incrementAndGet();
                } finally {
                    taskLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        taskLatch.await();
        executorService.shutdown();

        // Assert
        log.info("Success count: {}, Failure count: {}", successCount, failureCount);
        log.info("Success count: {}, {}, {}, {}", successCount1, successCount2, successCount3, successCount4);

        long expectedStock1 = INITIAL_STOCK - (successCount1.get() + successCount3.get());
        long expectedStock2 = INITIAL_STOCK - (successCount1.get() + successCount2.get());
        long expectedStock3 = INITIAL_STOCK - (successCount2.get() + successCount3.get());
        long expectedStock4 = INITIAL_STOCK - successCount4.get();

        ProductOption actual1 = productOptionRepository.findById(PRODUCT_OPTION1.getId());
        ProductOption actual2 = productOptionRepository.findById(PRODUCT_OPTION2.getId());
        ProductOption actual3 = productOptionRepository.findById(PRODUCT_OPTION3.getId());
        ProductOption actual4 = productOptionRepository.findById(PRODUCT_OPTION4.getId());

        log.info("PRODUCT_OPTION1 예상 재고: {}, 실제 재고: {}", expectedStock1, actual1.getStock());
        log.info("PRODUCT_OPTION2 예상 재고: {}, 실제 재고: {}", expectedStock2, actual2.getStock());
        log.info("PRODUCT_OPTION3 예상 재고: {}, 실제 재고: {}", expectedStock3, actual3.getStock());
        log.info("PRODUCT_OPTION4 예상 재고: {}, 실제 재고: {}", expectedStock4, actual4.getStock());

        assertThat(actual1.getStock()).isEqualTo(expectedStock1);
        assertThat(actual2.getStock()).isEqualTo(expectedStock2);
        assertThat(actual3.getStock()).isEqualTo(expectedStock3);
        assertThat(actual4.getStock()).isEqualTo(expectedStock4);

        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderItems.size()).isEqualTo(successCount1.get() * 2 + successCount2.get() * 2 + successCount2.get() * 2 + successCount4.get());
    }
}
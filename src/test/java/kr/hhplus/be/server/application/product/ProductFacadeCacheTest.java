package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductCriteria;
import kr.hhplus.be.server.application.product.dto.ProductResult;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.infra.cache.CacheType;
import kr.hhplus.be.server.surpport.container.RedisTestcontainersConfiguration;
import kr.hhplus.be.server.surpport.database.DatabaseClearExtension;
import kr.hhplus.be.server.surpport.database.RedisCacheClearExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
@ExtendWith(RedisCacheClearExtension.class)
@ActiveProfiles("test")
@DisplayName("[통합테스트] ProductFacadeCacheTest")
class ProductFacadeCacheTest extends RedisTestcontainersConfiguration {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Product PRODUCT1;
    private ProductOption PRODUCT1_OPTION1;
    private ProductOption PRODUCT1_OPTION2;

    private Product PRODUCT2;
    private ProductOption PRODUCT2_OPTION1;
    private ProductOption PRODUCT2_OPTION2;
    private ProductOption PRODUCT2_OPTION3;

    private Order ORDER1;
    private Order ORDER2;
    private Order ORDER3;


    @BeforeEach
    void setUp() {
        PRODUCT1 = productRepository.save(new Product("양반", "김"));
        PRODUCT1_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "들기름 김", 1000L, 1000L));
        PRODUCT1_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT1.getId(), "참기름 김", 1250L, 500L));

        PRODUCT2 = productRepository.save(new Product("엽기떡볶이", "떡볶이"));
        PRODUCT2_OPTION1 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "초보 맛", 12000L, 1000L));
        PRODUCT2_OPTION2 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "중간 맛", 12500L, 500L));
        PRODUCT2_OPTION3 = productOptionRepository.save(new ProductOption(PRODUCT2.getId(), "죽을 맛", 13000L, 150L));

        ORDER1 = orderRepository.save(new Order(1L, 0L));
        orderItemRepository.save(new OrderItem(ORDER1.getId(), PRODUCT1_OPTION1.getId(), 1000L, 10L));
        orderItemRepository.save(new OrderItem(ORDER1.getId(), PRODUCT1_OPTION2.getId(), 1250L, 30L));
        orderItemRepository.save(new OrderItem(ORDER1.getId(), PRODUCT1_OPTION2.getId(), 1250L, 10L));

        ORDER2 = orderRepository.save(new Order(1L, 0L));
        orderItemRepository.save(new OrderItem(ORDER2.getId(), PRODUCT1_OPTION1.getId(), 1000L, 10L));
        orderItemRepository.save(new OrderItem(ORDER2.getId(), PRODUCT2_OPTION1.getId(), 12000L, 50L));
        orderItemRepository.save(new OrderItem(ORDER2.getId(), PRODUCT2_OPTION2.getId(), 12500L, 40L));

        ORDER3 = orderRepository.save(new Order(1L, 0L));
        orderItemRepository.save(new OrderItem(ORDER3.getId(), PRODUCT2_OPTION1.getId(), 12000L, 20L));
        orderItemRepository.save(new OrderItem(ORDER3.getId(), PRODUCT2_OPTION2.getId(), 12500L, 30L));
        orderItemRepository.save(new OrderItem(ORDER3.getId(), PRODUCT2_OPTION3.getId(), 13000L, 40L));
        orderItemRepository.save(new OrderItem(ORDER3.getId(), PRODUCT1_OPTION2.getId(), 1250L, 50L));
    }

    @Test
    @DisplayName("(Look-Aside) 상품 정보 조회시 cache miss가 발생하면 DB에서 조회 후 캐시를 넣는다. 이후 조회는 캐시로부터 조회한다.")
    void findProduct() {
        // Arrange
        Long productId = 1L;
        ProductCriteria.Find criteria = new ProductCriteria.Find(productId);
        String cacheValue = CacheType.CacheName.PRODUCT;
        String cacheKey = "productId:" + productId;

        // Act & Assert
        assertThat(cacheManager.getCache(cacheValue).get(cacheKey)).isNull(); // 캐시 저장 전 null
        ProductResult.ProductAggregate result1 = productFacade.findProduct(criteria);

        assertThat(cacheManager.getCache(cacheValue).get(cacheKey)).isNotNull(); //캐시 저장 후 not null
        ProductResult.ProductAggregate result2 = productFacade.findProduct(criteria);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1.productId()).isEqualTo(productId);

        Long ttl = redisTemplate.getExpire(cacheValue + "::" + cacheKey, TimeUnit.MINUTES);
        assertThat(ttl).isGreaterThan(0L);
        assertThat(ttl).isLessThan(CacheType.PRODUCT.getTtlMinutes());
    }

    @Test
    @DisplayName("(Look-Aside) 인기 판매 상품 조회시 cache miss가 발생하면 DB에서 조회 후 캐시를 넣는다. 이후 조회는 캐시로부터 조회한다.")
    void findBest() {
        // Arrange
        Integer days = 3;
        Integer limit = 5;
        ProductCriteria.FindBest criteria = new ProductCriteria.FindBest(days, limit);
        String cacheValue = CacheType.CacheName.BEST_PRODUCT;
        String cacheKey = "best:days:" + days + ":limit:" + limit;

        // Act & Assert
        assertThat(cacheManager.getCache(cacheValue).get(cacheKey)).isNull(); // 캐시 저장 전 null
        ProductResult.ProductList result1 = productFacade.findBest(criteria);

        assertThat(cacheManager.getCache(cacheValue).get(cacheKey)).isNotNull(); // 캐시 저장 후 not null
        ProductResult.ProductList result2 = productFacade.findBest(criteria);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1.products().size()).isEqualTo(result2.products().size());

        Long ttl = redisTemplate.getExpire(cacheValue + "::" + cacheKey, TimeUnit.MINUTES);
        assertThat(ttl).isGreaterThan(0L);
        assertThat(ttl).isLessThan(CacheType.BEST_PRODUCT.getTtlMinutes());
    }

    @Test
    @DisplayName("(Write Around) 스케줄러 실행(20분 간격) 시 인기 판매 상품을 DB에서 조회 후 캐시의 새롭게 저장한다.")
    void refreshBestProductCache() throws InterruptedException {
        // Arrange
        Integer days = 3;
        Integer limit = 5;
        ProductCriteria.FindBest criteria = new ProductCriteria.FindBest(days, limit);
        String cacheValue = CacheType.CacheName.BEST_PRODUCT;
        String cacheKey = "best:days:" + days + ":limit:" + limit;

        // 기존 캐시 설정 및 만료시간 20분으로 변경
        productFacade.findBest(criteria);
        redisTemplate.expire(cacheValue + "::" + cacheKey, 20, TimeUnit.MINUTES);
        Long initialTtl = redisTemplate.getExpire(cacheValue + "::" + cacheKey, TimeUnit.MINUTES);

        // Act
        productFacade.refreshBestProductCache(criteria);
        Long refreshedTtl = redisTemplate.getExpire(cacheValue + "::" + cacheKey, TimeUnit.MINUTES);

        // Assert
        log.info("Initial Ttl: {}, Refreshed Ttl: {}", initialTtl, refreshedTtl);
        assertThat(cacheManager.getCache(cacheValue).get(cacheKey)).isNotNull();
        assertThat(refreshedTtl).isLessThanOrEqualTo(CacheType.BEST_PRODUCT.getTtlMinutes());
        assertThat(refreshedTtl).isGreaterThanOrEqualTo(initialTtl);
    }

}

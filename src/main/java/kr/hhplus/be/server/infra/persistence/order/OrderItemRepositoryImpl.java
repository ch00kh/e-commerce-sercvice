package kr.hhplus.be.server.infra.persistence.order;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository jpaRepository;

    @Override
    public Optional<OrderItem> findByOrderIdAndProductOptionId(Long orderId, Long productOptionId) {
        return jpaRepository.findByOrderIdAndProductOptionId(orderId, productOptionId);
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        return jpaRepository.save(orderItem);
    }

    @Override
    public List<OrderInfo.Best> findBestSelling(Integer days, Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<OrderItemJpaRepository.BestSellingProjection> bestSelling = jpaRepository.findBestSelling(startDate, pageRequest);

        return bestSelling.stream()
                .map(e -> new OrderInfo.Best(e.getProductOptionId(), e.getTotalSaleQuantity())).toList();
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderItem> findByProductOptionId(Long productOptionId) {
        return jpaRepository.findByProductOptionId(productOptionId);
    }
}

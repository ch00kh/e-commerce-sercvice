package kr.hhplus.be.server.infra.persistence.order;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}

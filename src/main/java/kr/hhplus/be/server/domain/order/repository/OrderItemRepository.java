package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderItem;

import java.util.Optional;

public interface OrderItemRepository {

    Optional<OrderItem> findByProductOptionId(Long productOptionId);

    OrderItem save(OrderItem orderItem);

}

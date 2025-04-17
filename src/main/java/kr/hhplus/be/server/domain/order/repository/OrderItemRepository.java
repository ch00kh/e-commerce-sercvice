package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository {

    Optional<OrderItem> findByProductOptionId(Long command);

    OrderItem save(OrderItem orderItem);

}

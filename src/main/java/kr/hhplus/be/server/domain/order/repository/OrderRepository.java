package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);
}

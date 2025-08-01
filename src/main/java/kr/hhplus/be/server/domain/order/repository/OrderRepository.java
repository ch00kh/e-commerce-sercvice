package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.Order;

public interface OrderRepository {
    Order save(Order order);

    Order findById(Long orderId);
}

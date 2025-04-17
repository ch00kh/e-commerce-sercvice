package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    Optional<OrderItem> findByOrderIdAndProductOptionId(Long orderId, Long productOptionId);

    OrderItem save(OrderItem orderItem);

    List<OrderInfo.Best> findBestSelling(Integer days, Integer limit);
}

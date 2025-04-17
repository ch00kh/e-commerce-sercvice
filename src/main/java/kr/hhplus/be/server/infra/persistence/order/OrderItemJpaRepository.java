package kr.hhplus.be.server.infra.persistence.order;


import kr.hhplus.be.server.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderIdAndProductOptionId(Long orderId, Long productOptionId);

}

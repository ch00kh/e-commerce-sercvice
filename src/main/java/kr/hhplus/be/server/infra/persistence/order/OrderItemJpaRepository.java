package kr.hhplus.be.server.infra.persistence.order;


import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderIdAndProductOptionId(Long orderId, Long productOptionId);

    @Query("SELECT new kr.hhplus.be.server.domain.order.dto.OrderInfo.Best(oi.productOptionId, SUM(oi.quantity)) " +
            "FROM OrderItem oi " +
            "WHERE oi.createdAt >= CURRENT_TIMESTAMP - :days " +
            "GROUP BY oi.productOptionId " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<OrderInfo.Best> findBestSelling(@Param("days") Integer days, Pageable pageable);

}

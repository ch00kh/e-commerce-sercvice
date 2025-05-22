package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventPublisher eventPublish;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderInfo.Create createOrder(OrderCommand.Create command) {

        Long totalAmount = command.orderItems().stream()
                .mapToLong(item -> item.unitPrice() * item.quantity())
                .sum();

        Order order = new Order(command.userId(), totalAmount);

        Order savedOrder = orderRepository.save(order);

        command.orderItems().forEach(item -> {
            orderItemRepository.save(
                    new OrderItem(
                            savedOrder.getId(),
                            item.productOptionId(),
                            item.unitPrice(),
                            item.quantity()
                    ));
                }
        );

        return new OrderInfo.Create(
                order.getId(),
                order.getUserId(),
                null,
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPaymentAmount()
            );
    }

    /**
     * 주문 상품 보류 (CREATED -> PENDING)
     */
    @Transactional
    public void holdOrders(OrderCommand.handleOrders command) {
        command.stockDetails().forEach(stock -> {
            if (!stock.canPurchase()) {
                OrderItem orderItem = orderItemRepository.findByOrderIdAndProductOptionId(command.orderId(), stock.optionId());
                orderItem.holdStatus();
            }
        });
    }

    /**
     * 쿠폰 적용
     */
    @Transactional
    public OrderInfo.Create applyCoupon(OrderCommand.UseCoupon command) {

        Order order = orderRepository.findById(command.orderId());

        if (command.couponId() == null) {
            return new OrderInfo.Create(
                    order.getId(),
                    order.getUserId(),
                    order.getIssuedCouponId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getDiscountAmount(),
                    order.getPaymentAmount()
            );
        }

        order.useCoupon(command.couponId(), command.discountPrice());

        OrderInfo.Create create = new OrderInfo.Create(
                order.getId(),
                order.getUserId(),
                order.getIssuedCouponId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPaymentAmount()
        );
        return create;
    }

    /**
     * 주문 조회
     */
    @Transactional(readOnly = true)
    public Order findById(OrderCommand.Find command) {
        return orderRepository.findById(command.orderId());
    }

    /**
     * 주문 상품 결제 (PAYED)
     */
    @Transactional
    public Order pay(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId());

        eventPublish.publishOrderComplete(
                new OrderEvent.OrderComplete(
                        order.getId(),
                        order.getUserId(),
                        order.getIssuedCouponId(),
                        order.getStatus(),
                        order.getPaymentAmount(),
                        order.getTotalAmount(),
                        order.getDiscountAmount()
                )
        );

        return order.pay();
    }

    /**
     * 인기 판매 상품 조회
     */
    @Transactional(readOnly = true)
    public List<OrderInfo.Best> findBestSelling(OrderCommand.FindBest command) {
        return orderItemRepository.findBestSelling(command.days(), command.limit());
    }

    /**
     * 주문 정보 전송 비동기 처리
     */
    public void sendOrder(OrderCommand.Send command) {
        log.info("Send OrderData to DataPlatform : {}", command);
    }
}

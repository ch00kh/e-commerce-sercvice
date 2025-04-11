package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderInfo.Create createOrder(OrderCommand.Create command) {

        Long totalAmount = command.orderItems().stream()
                .mapToLong(item -> item.unitPrice() * item.quantity())
                .sum();

        Order order = Order.builder()
                .userId(command.userId())
                .issuedCouponId(command.issuedCouponId())
                .totalAmount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        command.orderItems().forEach(item -> {
                    OrderItem orderItem = OrderItem.builder()
                            .orderId(savedOrder.getId())
                            .productOptionId(item.productOptionId())
                            .unitPrice(item.unitPrice())
                            .quantity(item.quantity())
                            .build();
                    orderItemRepository.save(orderItem);
                }
        );

        return OrderInfo.Create.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .paymentAmount(order.getPaymentAmount())
                .build();
    }

    @Transactional
    public void holdOrder(OrderCommand.HoldOrder command) {

        OrderItem orderItem = orderItemRepository.findByProductOptionId(command.productOptionId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        orderItem.holdStatus();
    }

    @Transactional
    public OrderInfo.Create useCoupon(OrderCommand.UseCoupon command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        order.useCoupon(command.couponId(), command.discountPrice());

        return OrderInfo.Create.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .issuedCouponId(order.getIssuedCouponId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .paymentAmount(order.getPaymentAmount())
                .build();
    }

    @Transactional(readOnly = true)
    public Order findById(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        if (order.getStatus() != OrderStatus.PAYED){
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }

        return order;
    }

    @Transactional
    public Order pay(OrderCommand.Find command) {

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return order.pay();
    }

    public void sendOrder(OrderCommand.Send build) {
        // 주문 정보 전송 비돟기 처리
    }
}

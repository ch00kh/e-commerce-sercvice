package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

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

    @Transactional
    public void holdOrder(OrderCommand.HoldOrder command) {

        OrderItem orderItem = orderItemRepository.findByOrderIdAndProductOptionId(command.orderId() ,command.productOptionId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        orderItem.holdStatus();
    }

    @Transactional
    public OrderInfo.Create useCoupon(OrderCommand.UseCoupon command) {

        if (command.couponId() == null) {
            return null;
        }

        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        order.useCoupon(command.couponId(), command.discountPrice());

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

    @Transactional(readOnly = true)
    public List<OrderInfo.Best> findBestSelling(OrderCommand.FindBest command) {
        return orderItemRepository.findBestSelling(command.days(), command.limit());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOrder(OrderCommand.Send build) {
        // 주문 정보 전송 비돟기 처리
    }
}

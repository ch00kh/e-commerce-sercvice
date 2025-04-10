package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final BalanceService balanceService;

    public PaymentResult.Pay pay(PaymentCriteria.Pay criteria) {

        // 결제 찾기 //
        Payment payment = paymentService.findPayment(criteria.toCommand());

        // 주문 찾기 //
        Order order = orderService.findById(new OrderCommand.Find(payment.getOrderId()));

        // 결제금액 차감
        balanceService.reduceBalance(new BalanceCommand.Reduce(order.getUserId(), order.getPaymentAmount()));

        // 결제 완료 //
        Payment pay = paymentService.pay(new PaymentCommand.Pay(criteria.paymentId(), order.getPaymentAmount()));

        // 주문 상태 변경 //
        order = orderService.pay(new OrderCommand.Find(payment.getOrderId()));

        // 주문 정보 전송
        orderService.sendOrder(OrderCommand.Send.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .issuedCouponId(order.getIssuedCouponId())
                .status(order.getStatus())
                .paymentAmount(order.getPaymentAmount())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .build()
        );

        return PaymentResult.Pay.builder()
                .id(pay.getId())
                .orderId(pay.getOrderId())
                .status(pay.getStatus())
                .amount(pay.getAmount())
                .paidAt(pay.getPaidAt())
                .build();

    }
}

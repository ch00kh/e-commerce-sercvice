package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.dto.BalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final BalanceService balanceService;

    @Transactional
    public PaymentResult.Pay pay(PaymentCriteria.Pay criteria) {

        // 결제 찾기
        Payment payment = paymentService.findPayment(criteria.toCommand());

        // 주문 찾기
        Order order = orderService.findById(new OrderCommand.Find(payment.getOrderId()));

        // 결제금액 차감
        Balance balance = balanceService.reduce(new BalanceCommand.Reduce(order.getUserId(),criteria.amount(), order.getIssuedCouponId()));

        // 결제 완료
        Payment pay = paymentService.pay(new PaymentCommand.Pay(payment.getId(), order.getPaymentAmount()));

        // 주문 상태 변경
        order = orderService.pay(new OrderCommand.Find(payment.getOrderId()));

        // 주문 정보 전송
        orderService.sendOrder(
                new OrderCommand.Send(
                        order.getId(),
                        order.getUserId(),
                        order.getIssuedCouponId(),
                        order.getStatus(),
                        order.getPaymentAmount(),
                        order.getTotalAmount(),
                        order.getDiscountAmount()
                )
        );

        return new PaymentResult.Pay(
                pay.getId(),
                pay.getOrderId(),
                balance.getBalance(),
                order.getStatus(),
                pay.getStatus(),
                pay.getAmount(),
                pay.getPaidAt()
        );
    }
}

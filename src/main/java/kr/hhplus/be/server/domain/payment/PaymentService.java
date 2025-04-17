package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment save(PaymentCommand.Save command) {

        return paymentRepository.save(new Payment(command.orderId(), command.amount()));
    }

    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.FindOrder command) {

        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return payment;
    }

    @Transactional
    public Payment pay(PaymentCommand.Pay command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return payment.pay(command.paymentAmount());
    }
}

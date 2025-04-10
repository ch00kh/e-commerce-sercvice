package kr.hhplus.be.server.domain.payment;

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

    @Transactional
    public Payment save(PaymentCommand.Save command) {

        Payment payment = Payment.builder()
                .orderId(command.orderId())
                .amount(command.amount())
                .build();

        return paymentRepository.save(payment);
    }

    public Payment findPayment(PaymentCommand.Find command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return payment;
    }

    public Payment pay(PaymentCommand.Pay command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return payment.pay(command.paymentAmount());
    }
}

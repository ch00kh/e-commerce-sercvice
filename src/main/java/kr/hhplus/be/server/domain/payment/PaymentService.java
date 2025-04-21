package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 결제 정보 저장
     */
    @Transactional
    public Payment save(PaymentCommand.Save command) {
        return paymentRepository.save(new Payment(command.orderId(), command.amount()));
    }

    /**
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.FindOrder command) {

        Payment payment = paymentRepository.findByOrderId(command.orderId());

        return payment;
    }

    /**
     * 결제
     */
    @Transactional
    public Payment pay(PaymentCommand.Pay command) {

        Payment payment = paymentRepository.findById(command.paymentId());

        return payment.pay(command.paymentAmount());
    }
}

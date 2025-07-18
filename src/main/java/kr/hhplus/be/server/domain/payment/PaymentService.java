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
    public Payment paymentCreate(PaymentCommand.Create command) {
        return paymentRepository.save(new Payment(command.orderId(), command.amount()));
    }

    /**
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public Payment findPayment(PaymentCommand.FindOrder command) {
        return paymentRepository.findByOrderId(command.orderId());
    }

    /**
     * 결제
     */
    @Transactional
    public Payment pay(PaymentCommand.Pay command) {
        Payment payment = paymentRepository.findByIdWithOptimisticLock(command.paymentId());
        return payment.pay(command.paymentAmount());
    }
}

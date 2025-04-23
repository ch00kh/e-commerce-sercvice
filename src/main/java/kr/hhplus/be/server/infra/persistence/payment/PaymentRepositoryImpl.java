package kr.hhplus.be.server.infra.persistence.payment;

import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long paymentId) {
        return jpaRepository.findById(paymentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }

}

package kr.hhplus.be.server.domain.payment.repository;

import kr.hhplus.be.server.domain.payment.entity.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment findById(Long paymentId);

    Payment findByOrderId(Long orderId);

    void deleteAll();
}

package kr.hhplus.be.server.infra.persistence.payment;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p From Payment p WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithOptimisticLock(Long paymentId);
}

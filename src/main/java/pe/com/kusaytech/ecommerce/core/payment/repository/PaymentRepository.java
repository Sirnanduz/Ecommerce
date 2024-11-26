package pe.com.kusaytech.ecommerce.core.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.com.kusaytech.ecommerce.core.payment.model.PaymentModel;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {
}
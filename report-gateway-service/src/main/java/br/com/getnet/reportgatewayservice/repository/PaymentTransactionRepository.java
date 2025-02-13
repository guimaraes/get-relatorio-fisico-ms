package br.com.getnet.reportgatewayservice.repository;

import br.com.getnet.reportgatewayservice.model.domain.PaymentTransaction;
import br.com.getnet.reportgatewayservice.model.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByCpfAndStatus(String cpf, PaymentStatus status);
}

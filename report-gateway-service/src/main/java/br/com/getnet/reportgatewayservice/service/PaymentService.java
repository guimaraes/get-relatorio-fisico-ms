package br.com.getnet.reportgatewayservice.service;

import br.com.getnet.reportgatewayservice.model.domain.PaymentTransaction;
import br.com.getnet.reportgatewayservice.model.domain.enums.PaymentStatus;
import br.com.getnet.reportgatewayservice.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void registerPayment(String cpf, BigDecimal amount) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setCpf(cpf);
        transaction.setAmount(amount);
        transaction.setStatus(PaymentStatus.PENDING);
        paymentTransactionRepository.save(transaction);

        rabbitTemplate.convertAndSend("paymentQueue", transaction);
    }

    @Transactional
    public void rollbackPayment(String cpf) {
        PaymentTransaction transaction = paymentTransactionRepository.findByCpfAndStatus(cpf, PaymentStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("No pending payment found for CPF: " + cpf));

        transaction.setStatus(PaymentStatus.REFUNDED);
        paymentTransactionRepository.save(transaction);
        rabbitTemplate.convertAndSend("refundQueue", transaction);
    }
}

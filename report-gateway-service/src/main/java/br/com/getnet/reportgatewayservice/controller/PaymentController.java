package br.com.getnet.reportgatewayservice.controller;

import br.com.getnet.reportgatewayservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerPayment(@RequestParam String cpf, @RequestParam BigDecimal amount) {
        paymentService.registerPayment(cpf, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollbackPayment(@RequestParam String cpf) {
        paymentService.rollbackPayment(cpf);
        return ResponseEntity.ok().build();
    }
}

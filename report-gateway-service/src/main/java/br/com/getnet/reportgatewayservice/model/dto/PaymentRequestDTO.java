package br.com.getnet.reportgatewayservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequestDTO {
    private String cpf;
    private BigDecimal amount;
    private String reportType;
}


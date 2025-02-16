package br.com.getnet.reportgatewayservice.model.dto;

import br.com.getnet.reportgatewayservice.model.domain.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportRequestDTO {

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @NotNull
    private ReportType type;

    @NotNull
    private BigDecimal amount;

    public boolean isFullReport() {
        return ReportType.COMPLETE.equals(this.type);
    }
}

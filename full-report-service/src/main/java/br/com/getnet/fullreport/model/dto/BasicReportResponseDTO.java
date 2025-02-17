package br.com.getnet.fullreport.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasicReportResponseDTO {
    private String cpf;
    private String name;
    private String gender;
    private String nationality;
}

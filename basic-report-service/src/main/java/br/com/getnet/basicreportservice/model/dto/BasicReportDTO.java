package br.com.getnet.basicreportservice.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicReportDTO {
    private String cpf;
    private String name;
    private String gender;
    private String nationality;
}

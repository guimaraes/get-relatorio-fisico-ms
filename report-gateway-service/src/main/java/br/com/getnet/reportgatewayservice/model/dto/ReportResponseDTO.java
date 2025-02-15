package br.com.getnet.reportgatewayservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private String name;
    private String gender;
    private String nationality;
    private String address;
    private String phone;
    private String document;

}

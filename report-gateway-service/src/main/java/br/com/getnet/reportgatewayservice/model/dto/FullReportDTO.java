package br.com.getnet.reportgatewayservice.model.dto;

import lombok.Data;

@Data
public class FullReportDTO {
    private String name;
    private String gender;
    private String nationality;
    private String address;
    private String phoneNumber;
    private String rg;
    private String cpf;
}

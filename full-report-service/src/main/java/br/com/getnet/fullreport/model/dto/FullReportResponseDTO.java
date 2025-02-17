package br.com.getnet.fullreport.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FullReportResponseDTO {
    private String cpf;
    private String name;
    private String gender;
    private String nationality;
    private String address;
    private String phoneNumber;
    private String rg;
    private String document;
}
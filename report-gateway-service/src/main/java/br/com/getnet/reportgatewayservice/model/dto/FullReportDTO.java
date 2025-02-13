package br.com.getnet.reportgatewayservice.model.dto;

import lombok.Data;

@Data
public class FullReportDTO {
    private String nome;
    private String sexo;
    private String nacionalidade;
    private String endereco;
    private String telefone;
    private String rg;
    private String cpf;
}

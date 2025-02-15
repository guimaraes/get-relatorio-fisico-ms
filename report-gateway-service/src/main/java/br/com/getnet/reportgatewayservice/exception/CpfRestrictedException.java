package br.com.getnet.reportgatewayservice.exception;

public class CpfRestrictedException extends RuntimeException {

    public CpfRestrictedException(String cpf) {
        super("The report cannot be generated because the CPF " + cpf + " is restricted.");
    }
}

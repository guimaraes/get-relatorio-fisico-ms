package br.com.getnet.reportgatewayservice.util;

public class CpfValidator {
    public static boolean isRestricted(String cpf) {
        return cpf.chars().map(Character::getNumericValue).sum() == 44;
    }
}

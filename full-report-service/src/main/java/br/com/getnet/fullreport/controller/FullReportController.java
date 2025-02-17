package br.com.getnet.fullreport.controller;

import br.com.getnet.fullreport.model.dto.FullReportResponseDTO;
import br.com.getnet.fullreport.service.FullReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/full-report")
@RequiredArgsConstructor
public class FullReportController {

    private final FullReportService fullReportService;


    @Operation(summary = "Gera um relatório físico", description = "Endpoint para geração de relatório físico com base em parâmetros.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<FullReportResponseDTO> getFullReport(
            @PathVariable @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos") String cpf) {

        FullReportResponseDTO response = fullReportService.getFullReport(cpf);
        return ResponseEntity.ok(response);
    }
}

package br.com.getnet.basicreportservice.controller;

import br.com.getnet.basicreportservice.model.dto.BasicReportResponseDTO;
import br.com.getnet.basicreportservice.service.BasicReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/basic-report")
@RequiredArgsConstructor
public class BasicReportController {

    private final BasicReportService service;

    @Operation(summary = "Gera um relatório físico", description = "Endpoint para geração de relatório físico com base em parâmetros.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<BasicReportResponseDTO> getBasicReport(@Valid @PathVariable String cpf) {
        return ResponseEntity.ok(service.getBasicReport(cpf));
    }
}

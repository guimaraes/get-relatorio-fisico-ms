package br.com.getnet.basicreportservice.controller;

import br.com.getnet.basicreportservice.model.dto.BasicReportResponseDTO;
import br.com.getnet.basicreportservice.service.BasicReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/basic-report")
@RequiredArgsConstructor
public class BasicReportController {

    private final BasicReportService service;

    @GetMapping("/{cpf}")
    public ResponseEntity<BasicReportResponseDTO> getBasicReport(@Valid @PathVariable String cpf) {
        return ResponseEntity.ok(service.getBasicReport(cpf));
    }
}

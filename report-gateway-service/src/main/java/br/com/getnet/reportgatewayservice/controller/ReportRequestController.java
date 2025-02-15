package br.com.getnet.reportgatewayservice.controller;

import br.com.getnet.reportgatewayservice.model.dto.ReportRequestDTO;
import br.com.getnet.reportgatewayservice.model.dto.ReportResponseDTO;
import br.com.getnet.reportgatewayservice.service.ReportRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportRequestController {

    private final ReportRequestService reportRequestService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> requestReport(@Valid @RequestBody ReportRequestDTO requestDTO) {
        ReportResponseDTO response = reportRequestService.requestReport(requestDTO);
        return ResponseEntity.ok(response);
    }
}

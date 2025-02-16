package br.com.getnet.reportgatewayservice.service.client;

import br.com.getnet.reportgatewayservice.model.dto.ReportResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fullReportClient", url = "${services.full-report-url}")
public interface FullReportClient {
    @GetMapping("/api/v1/full-report/{cpf}")
    ReportResponseDTO getFullReport(@PathVariable("cpf") String cpf);
}
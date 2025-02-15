package br.com.getnet.reportgatewayservice.service.client;

import br.com.getnet.reportgatewayservice.model.dto.ReportResponseDTO;
import br.com.getnet.reportgatewayservice.config.ServiceProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;

@FeignClient(name = "basic-report-service")
public interface BasicReportClient {
    @GetMapping("/api/v1/basic-report/{cpf}")
    ReportResponseDTO getBasicReport(@PathVariable("cpf") String cpf);
}






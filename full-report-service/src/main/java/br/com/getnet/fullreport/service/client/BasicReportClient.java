package br.com.getnet.fullreport.service.client;

import br.com.getnet.fullreport.model.dto.BasicReportResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "basicReportClient", url = "${services.basic-report-url}")
public interface BasicReportClient {

    @GetMapping("/api/v1/basic-report/{cpf}")
    BasicReportResponseDTO getBasicReport(@PathVariable("cpf") String cpf);
}

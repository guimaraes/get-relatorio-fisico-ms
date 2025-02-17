package br.com.getnet.fullreport.service;

import br.com.getnet.fullreport.exception.ReportNotFoundException;
import br.com.getnet.fullreport.model.domain.FullReport;
import br.com.getnet.fullreport.model.dto.BasicReportResponseDTO;
import br.com.getnet.fullreport.model.dto.FullReportResponseDTO;
import br.com.getnet.fullreport.repository.FullReportRepository;
import br.com.getnet.fullreport.service.client.BasicReportClient;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FullReportService {

    private final FullReportRepository fullReportRepository;
    private final BasicReportClient basicReportClient;

    @Retry(name = "full-report-service", fallbackMethod = "handleRetryFailure")
    @Transactional(readOnly = true)
    public FullReportResponseDTO getFullReport(String cpf) {
        FullReport fullReport = fullReportRepository.findByCpf(cpf)
                .orElseThrow(() -> new ReportNotFoundException("CPF não encontrado: " + cpf));

        BasicReportResponseDTO basicReport = basicReportClient.getBasicReport(cpf);

        return FullReportResponseDTO.builder()
                .cpf(fullReport.getCpf())
                .name(basicReport.getName())
                .gender(basicReport.getGender())
                .nationality(basicReport.getNationality())
                .address(fullReport.getAddress())
                .phoneNumber(fullReport.getPhoneNumber())
                .rg(fullReport.getRg())
                .document(fullReport.getDocument())
                .build();
    }

    private FullReportResponseDTO handleRetryFailure(String cpf, Exception ex) {
        throw new ReportNotFoundException("Falha ao buscar relatório após várias tentativas: " + ex.getMessage());
    }
}

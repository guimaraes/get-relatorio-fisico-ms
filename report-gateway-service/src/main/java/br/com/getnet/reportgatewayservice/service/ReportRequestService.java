package br.com.getnet.reportgatewayservice.service;

import br.com.getnet.reportgatewayservice.model.domain.enums.ReportStatus;
import br.com.getnet.reportgatewayservice.model.domain.enums.ReportType;
import br.com.getnet.reportgatewayservice.model.dto.ReportRequestDTO;
import br.com.getnet.reportgatewayservice.model.dto.ReportResponseDTO;
import br.com.getnet.reportgatewayservice.exception.CpfRestrictedException;
import br.com.getnet.reportgatewayservice.exception.ReportProcessingException;
import br.com.getnet.reportgatewayservice.model.domain.ReportRequest;
import br.com.getnet.reportgatewayservice.repository.ReportRequestRepository;
import br.com.getnet.reportgatewayservice.service.client.BasicReportClient;
import br.com.getnet.reportgatewayservice.service.client.FullReportClient;
import br.com.getnet.reportgatewayservice.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ReportRequestService {

    private final ReportRequestRepository reportRequestRepository;
    private final BasicReportClient basicReportClient;
    private final FullReportClient fullReportClient;
    private final PaymentService paymentService;

    @Transactional
    public ReportResponseDTO requestReport(ReportRequestDTO requestDTO) {
        if (CpfValidator.isRestricted(requestDTO.getCpf())) {
            throw new CpfRestrictedException("CPF is restricted and cannot generate reports.");
        }

        BigDecimal cost = requestDTO.isFullReport() ? BigDecimal.valueOf(10.00) : BigDecimal.valueOf(5.00);

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setCpf(requestDTO.getCpf());
        reportRequest.setType(requestDTO.isFullReport() ? ReportType.COMPLETE : ReportType.BASIC);
        reportRequest.setStatus(ReportStatus.PENDING);
        reportRequest.setAmount(cost);
        reportRequest.setCreatedAt(LocalDateTime.now());

        reportRequestRepository.save(reportRequest);

        if (requestDTO.isFullReport()) {
            return fetchFullReport(requestDTO.getCpf(), reportRequest);
        } else {
            return fetchBasicReport(requestDTO.getCpf(), reportRequest);
        }
    }


    private ReportResponseDTO fetchBasicReport(String cpf, ReportRequest reportRequest) {
        try {
            return basicReportClient.getBasicReport(cpf);
        } catch (Exception e) {
            handleFailure(reportRequest);
            throw new ReportProcessingException("Failed to generate the basic report.");
        }
    }

    private ReportResponseDTO fetchFullReport(String cpf, ReportRequest reportRequest) {
        CompletableFuture<ReportResponseDTO> basicReportFuture = CompletableFuture.supplyAsync(() -> basicReportClient.getBasicReport(cpf));
        CompletableFuture<ReportResponseDTO> fullReportFuture = CompletableFuture.supplyAsync(() -> fullReportClient.getFullReport(cpf));

        return CompletableFuture.allOf(basicReportFuture, fullReportFuture)
                .thenApply(voidRes -> consolidateReports(basicReportFuture, fullReportFuture, reportRequest))
                .exceptionally(ex -> {
                    handleFailure(reportRequest);
                    throw new ReportProcessingException("Failed to generate the full report.");
                })
                .join();
    }

    private ReportResponseDTO consolidateReports(CompletableFuture<ReportResponseDTO> basicFuture,
                                                 CompletableFuture<ReportResponseDTO> fullFuture,
                                                 ReportRequest reportRequest) {
        try {
            ReportResponseDTO basic = basicFuture.get();
            ReportResponseDTO full = fullFuture.get();

            full.setName(basic.getName());
            full.setGender(basic.getGender());
            full.setNationality(basic.getNationality());

            return full;
        } catch (Exception e) {
            handleFailure(reportRequest);
            throw new ReportProcessingException("Error consolidating report data.", e);
        }
    }


    private void handleFailure(ReportRequest reportRequest) {
        reportRequest.setStatus(ReportStatus.FAILED);
        reportRequestRepository.save(reportRequest);
        paymentService.rollbackPayment(reportRequest.getCpf());
    }
}

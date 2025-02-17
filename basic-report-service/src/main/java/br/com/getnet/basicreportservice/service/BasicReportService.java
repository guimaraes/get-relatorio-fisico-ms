package br.com.getnet.basicreportservice.service;

import br.com.getnet.basicreportservice.model.domain.BasicReport;
import br.com.getnet.basicreportservice.model.dto.BasicReportRequestDTO;
import br.com.getnet.basicreportservice.model.dto.BasicReportResponseDTO;
import br.com.getnet.basicreportservice.repository.BasicReportRepository;
import br.com.getnet.basicreportservice.exception.BasicReportNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReportService {

    private final BasicReportRepository repository;

    @Transactional(readOnly = true)
    public BasicReportResponseDTO getBasicReport(String cpf) {
        BasicReport report = repository.findByCpf(cpf)
                .orElseThrow(() -> new BasicReportNotFoundException("CPF não encontrado: " + cpf));

        return BasicReportResponseDTO.builder()
                .cpf(report.getCpf())
                .name(report.getName())
                .gender(report.getGender())
                .nationality(report.getNationality())
                .build();
    }
}

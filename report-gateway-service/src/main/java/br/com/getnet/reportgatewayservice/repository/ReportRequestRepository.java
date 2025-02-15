package br.com.getnet.reportgatewayservice.repository;

import br.com.getnet.reportgatewayservice.model.domain.ReportRequest;
import br.com.getnet.reportgatewayservice.model.domain.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, Long> {
    Optional<ReportRequest> findByCpfAndStatus(String cpf, ReportStatus status);
    List<ReportRequest> findByStatus(ReportStatus status);
}

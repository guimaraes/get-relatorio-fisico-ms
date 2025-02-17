package br.com.getnet.fullreport.repository;

import br.com.getnet.fullreport.model.domain.FullReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FullReportRepository extends JpaRepository<FullReport, Long> {
    Optional<FullReport> findByCpf(String cpf);
}

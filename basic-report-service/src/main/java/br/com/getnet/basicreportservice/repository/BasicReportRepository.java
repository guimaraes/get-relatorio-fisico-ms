package br.com.getnet.basicreportservice.repository;

import br.com.getnet.basicreportservice.model.domain.BasicReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasicReportRepository extends JpaRepository<BasicReport, Long> {
    Optional<BasicReport> findByCpf(String cpf);
}

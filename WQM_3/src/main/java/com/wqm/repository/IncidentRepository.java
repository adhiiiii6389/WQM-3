package com.wqm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wqm.entity.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByLineIdOrderByCreatedAtDesc(Long lineId);

    Optional<Incident> findFirstByLineIdAndMetricAndStatusIgnoreCaseOrderByCreatedAtDesc(
            Long lineId,
            String metric,
            String status);
}

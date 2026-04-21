package com.wqm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wqm.entity.Incident;
import com.wqm.entity.Line;
import com.wqm.repository.IncidentRepository;
import com.wqm.repository.LineRepository;

@Service
@Transactional
public class IncidentService {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_RESOLVED = "RESOLVED";

    private final IncidentRepository incidentRepository;
    private final LineRepository lineRepository;

    public IncidentService(IncidentRepository incidentRepository, LineRepository lineRepository) {
        this.incidentRepository = incidentRepository;
        this.lineRepository = lineRepository;
    }

    public List<Incident> createIncidentsFromCheck(QualityCheckResult checkResult) {
        List<Incident> created = new ArrayList<>();
        if (checkResult == null || !checkResult.hasViolations()) {
            return created;
        }

        Line line = lineRepository.findById(checkResult.getLineId())
                .orElseThrow(() -> new IllegalArgumentException("Line not found: " + checkResult.getLineId()));

        for (ViolationDetail violation : checkResult.getViolations()) {
            if (hasActiveIncident(line.getId(), violation.getMetric())) {
                continue;
            }

            Incident incident = new Incident();
            incident.setLine(line);
            incident.setMetric(violation.getMetric());
            incident.setReadingValue(violation.getReadingValue());
            incident.setThresholdValue(violation.getThresholdValue());
            incident.setStatus(STATUS_ACTIVE);
            incident.setMessage(buildMessage(violation));
            incident.setCreatedAt(LocalDateTime.now());
            created.add(incidentRepository.save(incident));
        }

        return created;
    }

    public Incident updateStatus(Long incidentId, String status) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        String normalizedStatus = status == null ? STATUS_ACTIVE : status.trim().toUpperCase();
        incident.setStatus(normalizedStatus);

        if (STATUS_RESOLVED.equals(normalizedStatus)) {
            incident.setResolvedAt(LocalDateTime.now());
        } else {
            incident.setResolvedAt(null);
        }

        return incidentRepository.save(incident);
    }

    public boolean hasActiveIncident(Long lineId, String metric) {
        return incidentRepository
                .findFirstByLineIdAndMetricAndStatusIgnoreCaseOrderByCreatedAtDesc(lineId, metric, STATUS_ACTIVE)
                .isPresent();
    }

    private String buildMessage(ViolationDetail violation) {
        return violation.getMetric() + " is out of range (rule=" + violation.getRule() + ")";
    }
}

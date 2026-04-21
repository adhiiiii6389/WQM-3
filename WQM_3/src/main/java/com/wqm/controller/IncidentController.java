package com.wqm.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.wqm.entity.Incident;
import com.wqm.entity.Line;
import com.wqm.repository.IncidentRepository;
import com.wqm.repository.LineRepository;
import com.wqm.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentRepository incidentRepository;
    private final LineRepository lineRepository;
    private final IncidentService incidentService;

    public IncidentController(
            IncidentRepository incidentRepository,
            LineRepository lineRepository,
            IncidentService incidentService) {
        this.incidentRepository = incidentRepository;
        this.lineRepository = lineRepository;
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<Incident> getAll(@RequestParam(required = false) Long lineId) {
        return lineId == null
                ? incidentRepository.findAll()
                : incidentRepository.findByLineIdOrderByCreatedAtDesc(lineId);
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incident create(@RequestBody Incident incident) {
        incident.setId(null);
        applyLine(incident);
        incident.setStatus(incident.getStatus() == null ? IncidentService.STATUS_ACTIVE : incident.getStatus().toUpperCase());
        incident.setCreatedAt(incident.getCreatedAt() == null ? LocalDateTime.now() : incident.getCreatedAt());
        return incidentRepository.save(incident);
    }

    @PutMapping("/{id}")
    public Incident update(@PathVariable Long id, @RequestBody Incident request) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));

        incident.setLine(request.getLine());
        incident.setMetric(request.getMetric());
        incident.setReadingValue(request.getReadingValue());
        incident.setThresholdValue(request.getThresholdValue());
        incident.setStatus(request.getStatus() == null ? IncidentService.STATUS_ACTIVE : request.getStatus().toUpperCase());
        incident.setMessage(request.getMessage());
        incident.setCreatedAt(request.getCreatedAt() == null ? LocalDateTime.now() : request.getCreatedAt());
        incident.setResolvedAt(request.getResolvedAt());
        applyLine(incident);
        return incidentRepository.save(incident);
    }

    @PatchMapping("/{id}/status")
    public Incident updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return incidentService.updateStatus(id, request.get("status"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found");
        }
        incidentRepository.deleteById(id);
    }

    private void applyLine(Incident incident) {
        if (incident.getLine() == null || incident.getLine().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line id is required");
        }

        Line line = lineRepository.findById(incident.getLine().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line not found"));
        incident.setLine(line);
    }
}

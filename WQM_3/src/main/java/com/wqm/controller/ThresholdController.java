package com.wqm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.wqm.entity.Line;
import com.wqm.entity.Threshold;
import com.wqm.repository.LineRepository;
import com.wqm.repository.ThresholdRepository;

@RestController
@RequestMapping("/api/thresholds")
public class ThresholdController {

    private final ThresholdRepository thresholdRepository;
    private final LineRepository lineRepository;

    public ThresholdController(ThresholdRepository thresholdRepository, LineRepository lineRepository) {
        this.thresholdRepository = thresholdRepository;
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public List<Threshold> getAll(@RequestParam(required = false) Long lineId) {
        if (lineId == null) {
            return thresholdRepository.findAll();
        }

        return thresholdRepository.findFirstByLineIdOrderByIdDesc(lineId)
                .stream().toList();
    }

    @GetMapping("/{id}")
    public Threshold getById(@PathVariable Long id) {
        return thresholdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Threshold not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Threshold create(@RequestBody Threshold threshold) {
        threshold.setId(null);
        applyLine(threshold);
        return thresholdRepository.save(threshold);
    }

    @PutMapping("/{id}")
    public Threshold update(@PathVariable Long id, @RequestBody Threshold request) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Threshold not found"));
        threshold.setLine(request.getLine());
        threshold.setMinPh(request.getMinPh());
        threshold.setMaxPh(request.getMaxPh());
        threshold.setMinTurbidity(request.getMinTurbidity());
        threshold.setMaxTurbidity(request.getMaxTurbidity());
        threshold.setMinConductivity(request.getMinConductivity());
        threshold.setMaxConductivity(request.getMaxConductivity());
        applyLine(threshold);
        return thresholdRepository.save(threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!thresholdRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Threshold not found");
        }
        thresholdRepository.deleteById(id);
    }

    private void applyLine(Threshold threshold) {
        if (threshold.getLine() == null || threshold.getLine().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line id is required");
        }

        Line line = lineRepository.findById(threshold.getLine().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line not found"));
        threshold.setLine(line);
    }
}

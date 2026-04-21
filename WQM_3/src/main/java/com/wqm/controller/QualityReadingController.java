package com.wqm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.wqm.entity.QualityReading;
import com.wqm.entity.WaterSensor;
import com.wqm.repository.LineRepository;
import com.wqm.repository.QualityReadingRepository;
import com.wqm.repository.WaterSensorRepository;

@RestController
@RequestMapping("/api/readings")
public class QualityReadingController {

    private final QualityReadingRepository qualityReadingRepository;
    private final LineRepository lineRepository;
    private final WaterSensorRepository waterSensorRepository;

    public QualityReadingController(
            QualityReadingRepository qualityReadingRepository,
            LineRepository lineRepository,
            WaterSensorRepository waterSensorRepository) {
        this.qualityReadingRepository = qualityReadingRepository;
        this.lineRepository = lineRepository;
        this.waterSensorRepository = waterSensorRepository;
    }

    @GetMapping
    public List<QualityReading> getAll() {
        return qualityReadingRepository.findAll();
    }

    @GetMapping("/{id}")
    public QualityReading getById(@PathVariable Long id) {
        return qualityReadingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading not found"));
    }

    @GetMapping("/line/{lineId}")
    public List<QualityReading> getByLineAndTimeWindow(
            @PathVariable Long lineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTs,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTs) {
        return qualityReadingRepository.findByLineIdAndTsBetweenOrderByTsDesc(lineId, startTs, endTs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualityReading create(@RequestBody QualityReading reading) {
        reading.setId(null);
        applyRelations(reading);
        reading.setTs(reading.getTs() == null ? LocalDateTime.now() : reading.getTs());
        return qualityReadingRepository.save(reading);
    }

    @PutMapping("/{id}")
    public QualityReading update(
            @PathVariable Long id,
            @RequestBody QualityReading request) {
        QualityReading reading = qualityReadingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading not found"));

        reading.setLine(request.getLine());
        reading.setSensor(request.getSensor());
        reading.setPh(request.getPh());
        reading.setTurbidity(request.getTurbidity());
        reading.setConductivity(request.getConductivity());
        reading.setTs(request.getTs() == null ? LocalDateTime.now() : request.getTs());
        applyRelations(reading);
        return qualityReadingRepository.save(reading);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!qualityReadingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading not found");
        }
        qualityReadingRepository.deleteById(id);
    }

    private void applyRelations(QualityReading reading) {
        if (reading.getLine() == null || reading.getLine().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line id is required");
        }

        Line line = lineRepository.findById(reading.getLine().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line not found"));

        WaterSensor sensor = null;
        if (reading.getSensor() != null && reading.getSensor().getId() != null) {
            sensor = waterSensorRepository.findById(reading.getSensor().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensor not found"));
        }

        reading.setLine(line);
        reading.setSensor(sensor);
    }
}

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
import com.wqm.entity.WaterSensor;
import com.wqm.repository.LineRepository;
import com.wqm.repository.WaterSensorRepository;

@RestController
@RequestMapping("/api/sensors")
public class WaterSensorController {

    private final WaterSensorRepository waterSensorRepository;
    private final LineRepository lineRepository;

    public WaterSensorController(WaterSensorRepository waterSensorRepository, LineRepository lineRepository) {
        this.waterSensorRepository = waterSensorRepository;
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public List<WaterSensor> getAll(@RequestParam(required = false) Long lineId) {
        return lineId == null
                ? waterSensorRepository.findAll()
                : waterSensorRepository.findByLineId(lineId);
    }

    @GetMapping("/{id}")
    public WaterSensor getById(@PathVariable Long id) {
        return waterSensorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaterSensor create(@RequestBody WaterSensor sensor) {
        sensor.setId(null);
        applyLine(sensor);
        return waterSensorRepository.save(sensor);
    }

    @PutMapping("/{id}")
    public WaterSensor update(@PathVariable Long id, @RequestBody WaterSensor request) {
        WaterSensor sensor = waterSensorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
        sensor.setSensorCode(request.getSensorCode());
        sensor.setSensorType(request.getSensorType());
        sensor.setBaselinePh(request.getBaselinePh());
        sensor.setBaselineTurbidity(request.getBaselineTurbidity());
        sensor.setBaselineConductivity(request.getBaselineConductivity());
        sensor.setLine(request.getLine());
        applyLine(sensor);
        return waterSensorRepository.save(sensor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!waterSensorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found");
        }
        waterSensorRepository.deleteById(id);
    }

    private void applyLine(WaterSensor sensor) {
        if (sensor.getLine() == null || sensor.getLine().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line id is required");
        }

        Line line = lineRepository.findById(sensor.getLine().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Line not found"));
        sensor.setLine(line);
    }
}

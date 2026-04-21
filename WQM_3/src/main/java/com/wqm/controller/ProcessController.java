package com.wqm.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wqm.entity.Line;
import com.wqm.entity.WaterSensor;
import com.wqm.repository.LineRepository;
import com.wqm.repository.QualityReadingRepository;
import com.wqm.repository.WaterSensorRepository;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private final LineRepository lineRepository;
    private final QualityReadingRepository qualityReadingRepository;
    private final WaterSensorRepository waterSensorRepository;

    public ProcessController(
            LineRepository lineRepository,
            QualityReadingRepository qualityReadingRepository,
            WaterSensorRepository waterSensorRepository) {
        this.lineRepository = lineRepository;
        this.qualityReadingRepository = qualityReadingRepository;
        this.waterSensorRepository = waterSensorRepository;
    }

    @GetMapping("/lines/out-of-spec")
    public List<Line> getLinesOutOfSpec() {
        return lineRepository.findLinesOutOfSpec();
    }

    @GetMapping("/sensor-drift")
    public List<Map<String, Object>> getSensorDrift(
            @RequestParam Long lineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTs,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTs) {

        List<Object[]> avgRows = qualityReadingRepository.findSensorAveragesForDrift(lineId, startTs, endTs);
        Map<Long, WaterSensor> sensorMap = new HashMap<>();
        for (WaterSensor sensor : waterSensorRepository.findByLineId(lineId)) {
            sensorMap.put(sensor.getId(), sensor);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : avgRows) {
            Long sensorId = (Long) row[0];
            Double avgPh = (Double) row[1];
            Double avgTurbidity = (Double) row[2];
            Double avgConductivity = (Double) row[3];

            WaterSensor sensor = sensorMap.get(sensorId);
            Double baselinePh = sensor == null ? null : sensor.getBaselinePh();
            Double baselineTurbidity = sensor == null ? null : sensor.getBaselineTurbidity();
            Double baselineConductivity = sensor == null ? null : sensor.getBaselineConductivity();

            Map<String, Object> item = new HashMap<>();
            item.put("sensorId", sensorId);
            item.put("sensorCode", sensor == null ? null : sensor.getSensorCode());
            item.put("avgPh", PrecisionUtil.round3(avgPh));
            item.put("baselinePh", PrecisionUtil.round3(baselinePh));
            item.put("phDelta", PrecisionUtil.round3(delta(avgPh, baselinePh)));
            item.put("avgTurbidity", PrecisionUtil.round3(avgTurbidity));
            item.put("baselineTurbidity", PrecisionUtil.round3(baselineTurbidity));
            item.put("turbidityDelta", PrecisionUtil.round3(delta(avgTurbidity, baselineTurbidity)));
            item.put("avgConductivity", PrecisionUtil.round3(avgConductivity));
            item.put("baselineConductivity", PrecisionUtil.round3(baselineConductivity));
            item.put("conductivityDelta", PrecisionUtil.round3(delta(avgConductivity, baselineConductivity)));
            result.add(item);
        }
        return result;
    }

    private Double delta(Double avgValue, Double baseline) {
        if (avgValue == null || baseline == null) {
            return null;
        }
        return avgValue - baseline;
    }
}

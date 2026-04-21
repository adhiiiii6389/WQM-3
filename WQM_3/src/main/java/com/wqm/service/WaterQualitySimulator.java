package com.wqm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wqm.entity.Incident;
import com.wqm.entity.Line;
import com.wqm.entity.QualityReading;
import com.wqm.entity.Threshold;
import com.wqm.entity.WaterSensor;
import com.wqm.repository.LineRepository;
import com.wqm.repository.QualityReadingRepository;
import com.wqm.repository.ThresholdRepository;
import com.wqm.repository.WaterSensorRepository;

@Component
public class WaterQualitySimulator {

    private final LineRepository lineRepository;
    private final WaterSensorRepository waterSensorRepository;
    private final ThresholdRepository thresholdRepository;
    private final QualityReadingRepository qualityReadingRepository;
    private final QualityService qualityService;
    private final IncidentService incidentService;
    private final Random random = new Random();
    private final AtomicInteger cycleCounter = new AtomicInteger();

    public WaterQualitySimulator(
            LineRepository lineRepository,
            WaterSensorRepository waterSensorRepository,
            ThresholdRepository thresholdRepository,
            QualityReadingRepository qualityReadingRepository,
            QualityService qualityService,
            IncidentService incidentService) {
        this.lineRepository = lineRepository;
        this.waterSensorRepository = waterSensorRepository;
        this.thresholdRepository = thresholdRepository;
        this.qualityReadingRepository = qualityReadingRepository;
        this.qualityService = qualityService;
        this.incidentService = incidentService;
    }

    @Scheduled(initialDelayString = "${quality.simulator.initial-delay-ms:5000}", fixedDelayString = "${quality.simulator.delay-ms:15000}")
    @Transactional
    public void runCycle() {
        int cycle = cycleCounter.incrementAndGet();
        System.out.println("[Simulator] Cycle " + cycle + " started");

        Line line = ensureDemoLine();
        WaterSensor sensor = ensureDemoSensor(line);
        Threshold threshold = ensureDemoThreshold(line);

        QualityReading reading = buildReading(line, sensor, threshold, cycle);
        QualityReading savedReading = qualityReadingRepository.save(reading);

        System.out.println(
                "[Simulator] Saved reading for line " + line.getName()
                        + " => ph=" + savedReading.getPh()
                        + ", turbidity=" + savedReading.getTurbidity()
                        + ", conductivity=" + savedReading.getConductivity());

        QualityCheckResult result = qualityService.Check(savedReading);
        List<Incident> createdIncidents = incidentService.createIncidentsFromCheck(result);

        if (result.hasViolations()) {
            System.out.println("[Simulator] Violations detected: " + result.getViolations().size());
        } else {
            System.out.println("[Simulator] No violations for this cycle");
        }

        for (Incident incident : createdIncidents) {
            System.out.println(
                    "[Simulator] Incident created => line=" + incident.getLine().getName()
                            + ", metric=" + incident.getMetric()
                            + ", value=" + incident.getReadingValue()
                            + ", threshold=" + incident.getThresholdValue());
        }

        System.out.println("[Simulator] Cycle " + cycle + " finished");
    }

    private Line ensureDemoLine() {
        return lineRepository.findAll().stream().findFirst().orElseGet(() -> {
            Line line = new Line();
            line.setName("Demo Line");
            line.setLocation("Default Plant");
            line.setActive(true);
            return lineRepository.save(line);
        });
    }

    private WaterSensor ensureDemoSensor(Line line) {
        List<WaterSensor> sensors = waterSensorRepository.findByLineId(line.getId());
        if (!sensors.isEmpty()) {
            return sensors.get(0);
        }

        WaterSensor sensor = new WaterSensor();
        sensor.setLine(line);
        sensor.setSensorCode("SENSOR-DEMO-1");
        sensor.setSensorType("MULTI");
        sensor.setBaselinePh(7.2);
        sensor.setBaselineTurbidity(1.0);
        sensor.setBaselineConductivity(250.0);
        return waterSensorRepository.save(sensor);
    }

    private Threshold ensureDemoThreshold(Line line) {
        return thresholdRepository.findFirstByLineIdOrderByIdDesc(line.getId()).orElseGet(() -> {
            Threshold threshold = new Threshold();
            threshold.setLine(line);
            threshold.setMinPh(6.5);
            threshold.setMaxPh(8.5);
            threshold.setMinTurbidity(0.0);
            threshold.setMaxTurbidity(5.0);
            threshold.setMinConductivity(100.0);
            threshold.setMaxConductivity(500.0);
            return thresholdRepository.save(threshold);
        });
    }

    private QualityReading buildReading(Line line, WaterSensor sensor, Threshold threshold, int cycle) {
        QualityReading reading = new QualityReading();
        reading.setLine(line);
        reading.setSensor(sensor);
        reading.setTs(LocalDateTime.now());

        double ph = randomBetween(threshold.getMinPh() + 0.1, threshold.getMaxPh() - 0.1);
        double turbidity = randomBetween(threshold.getMinTurbidity() + 0.1, threshold.getMaxTurbidity() - 0.2);
        double conductivity = randomBetween(threshold.getMinConductivity() + 10, threshold.getMaxConductivity() - 10);

        if (cycle % 4 == 0) {
            ph = threshold.getMinPh() - 0.3;
        }
        if (cycle % 6 == 0) {
            turbidity = threshold.getMaxTurbidity() + 0.8;
        }
        if (cycle % 9 == 0) {
            conductivity = threshold.getMaxConductivity() + 25.0;
        }

        reading.setPh(round3(ph));
        reading.setTurbidity(round3(turbidity));
        reading.setConductivity(round3(conductivity));
        return reading;
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private Double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
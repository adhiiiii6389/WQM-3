package com.wqm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wqm.entity.QualityReading;
import com.wqm.entity.Threshold;
import com.wqm.repository.QualityReadingRepository;
import com.wqm.repository.ThresholdRepository;

@Service
public class QualityService {

    private final QualityReadingRepository qualityReadingRepository;
    private final ThresholdRepository thresholdRepository;

    public QualityService(QualityReadingRepository qualityReadingRepository, ThresholdRepository thresholdRepository) {
        this.qualityReadingRepository = qualityReadingRepository;
        this.thresholdRepository = thresholdRepository;
    }

    public QualityCheckResult Check(Long lineId) {
        Optional<QualityReading> latestReading = qualityReadingRepository.findFirstByLineIdOrderByTsDesc(lineId);
        if (latestReading.isEmpty()) {
            return QualityCheckResult.noReading(lineId);
        }
        return Check(latestReading.get());
    }

    public QualityCheckResult Check(QualityReading reading) {
        Long lineId = reading.getLine().getId();
        Optional<Threshold> thresholdOpt = thresholdRepository.findFirstByLineIdOrderByIdDesc(lineId);
        if (thresholdOpt.isEmpty()) {
            return QualityCheckResult.missingThreshold(lineId, reading.getId(), reading.getTs());
        }

        Threshold threshold = thresholdOpt.get();
        List<ViolationDetail> violations = new ArrayList<>();
        addViolationIfOutOfRange(violations, "ph", reading.getPh(), threshold.getMinPh(), threshold.getMaxPh());
        addViolationIfOutOfRange(violations, "turbidity", reading.getTurbidity(), threshold.getMinTurbidity(), threshold.getMaxTurbidity());
        addViolationIfOutOfRange(violations, "conductivity", reading.getConductivity(), threshold.getMinConductivity(), threshold.getMaxConductivity());

        QualityCheckResult result = new QualityCheckResult();
        result.setLineId(lineId);
        result.setReadingId(reading.getId());
        result.setReadingTime(reading.getTs());
        result.setThresholdConfigured(true);
        result.setViolations(violations);
        result.setMessage(violations.isEmpty() ? "Reading is within thresholds." : "Threshold violations found.");
        return result;
    }

    private void addViolationIfOutOfRange(
            List<ViolationDetail> violations,
            String metric,
            Double value,
            Double min,
            Double max) {
        if (value == null || min == null || max == null) {
            return;
        }

        if (value < min) {
            violations.add(new ViolationDetail(metric, value, min, "LOWER_THAN_MIN"));
            return;
        }

        if (value > max) {
            violations.add(new ViolationDetail(metric, value, max, "HIGHER_THAN_MAX"));
        }
    }
}

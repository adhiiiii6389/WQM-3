package com.wqm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QualityCheckResult {

    private Long lineId;
    private Long readingId;
    private LocalDateTime readingTime;
    private boolean thresholdConfigured;
    private String message;
    private List<ViolationDetail> violations = new ArrayList<>();

    public static QualityCheckResult noReading(Long lineId) {
        QualityCheckResult result = new QualityCheckResult();
        result.setLineId(lineId);
        result.setThresholdConfigured(false);
        result.setMessage("No reading found for this line.");
        return result;
    }

    public static QualityCheckResult missingThreshold(Long lineId, Long readingId, LocalDateTime readingTime) {
        QualityCheckResult result = new QualityCheckResult();
        result.setLineId(lineId);
        result.setReadingId(readingId);
        result.setReadingTime(readingTime);
        result.setThresholdConfigured(false);
        result.setMessage("Threshold is not configured for this line.");
        return result;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getReadingId() {
        return readingId;
    }

    public void setReadingId(Long readingId) {
        this.readingId = readingId;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public boolean isThresholdConfigured() {
        return thresholdConfigured;
    }

    public void setThresholdConfigured(boolean thresholdConfigured) {
        this.thresholdConfigured = thresholdConfigured;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ViolationDetail> getViolations() {
        return violations;
    }

    public void setViolations(List<ViolationDetail> violations) {
        this.violations = violations;
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }
}

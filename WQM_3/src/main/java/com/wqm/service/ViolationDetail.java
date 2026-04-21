package com.wqm.service;

public class ViolationDetail {

    private String metric;
    private Double readingValue;
    private Double thresholdValue;
    private String rule;

    public ViolationDetail() {
    }

    public ViolationDetail(String metric, Double readingValue, Double thresholdValue, String rule) {
        this.metric = metric;
        this.readingValue = readingValue;
        this.thresholdValue = thresholdValue;
        this.rule = rule;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(Double readingValue) {
        this.readingValue = readingValue;
    }

    public Double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}

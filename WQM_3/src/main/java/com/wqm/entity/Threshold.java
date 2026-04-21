package com.wqm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "thresholds")
public class Threshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    private Double minPh;

    private Double maxPh;

    private Double minTurbidity;

    private Double maxTurbidity;

    private Double minConductivity;

    private Double maxConductivity;

    public Threshold() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Double getMinPh() {
        return minPh;
    }

    public void setMinPh(Double minPh) {
        this.minPh = minPh;
    }

    public Double getMaxPh() {
        return maxPh;
    }

    public void setMaxPh(Double maxPh) {
        this.maxPh = maxPh;
    }

    public Double getMinTurbidity() {
        return minTurbidity;
    }

    public void setMinTurbidity(Double minTurbidity) {
        this.minTurbidity = minTurbidity;
    }

    public Double getMaxTurbidity() {
        return maxTurbidity;
    }

    public void setMaxTurbidity(Double maxTurbidity) {
        this.maxTurbidity = maxTurbidity;
    }

    public Double getMinConductivity() {
        return minConductivity;
    }

    public void setMinConductivity(Double minConductivity) {
        this.minConductivity = minConductivity;
    }

    public Double getMaxConductivity() {
        return maxConductivity;
    }

    public void setMaxConductivity(Double maxConductivity) {
        this.maxConductivity = maxConductivity;
    }
}

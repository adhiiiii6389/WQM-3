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
@Table(name = "water_sensors")
public class WaterSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String sensorCode;

    @Column(nullable = false, length = 50)
    private String sensorType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    private Double baselinePh;

    private Double baselineTurbidity;

    private Double baselineConductivity;

    public WaterSensor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSensorCode() {
        return sensorCode;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Double getBaselinePh() {
        return baselinePh;
    }

    public void setBaselinePh(Double baselinePh) {
        this.baselinePh = baselinePh;
    }

    public Double getBaselineTurbidity() {
        return baselineTurbidity;
    }

    public void setBaselineTurbidity(Double baselineTurbidity) {
        this.baselineTurbidity = baselineTurbidity;
    }

    public Double getBaselineConductivity() {
        return baselineConductivity;
    }

    public void setBaselineConductivity(Double baselineConductivity) {
        this.baselineConductivity = baselineConductivity;
    }
}

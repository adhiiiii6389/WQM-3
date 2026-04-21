package com.wqm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wqm.entity.WaterSensor;

public interface WaterSensorRepository extends JpaRepository<WaterSensor, Long> {

    List<WaterSensor> findByLineId(Long lineId);
}

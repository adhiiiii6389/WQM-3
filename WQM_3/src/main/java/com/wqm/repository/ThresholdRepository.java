package com.wqm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wqm.entity.Threshold;

public interface ThresholdRepository extends JpaRepository<Threshold, Long> {

    Optional<Threshold> findFirstByLineIdOrderByIdDesc(Long lineId);
}

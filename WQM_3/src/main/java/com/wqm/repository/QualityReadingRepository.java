package com.wqm.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wqm.entity.QualityReading;

public interface QualityReadingRepository extends JpaRepository<QualityReading, Long> {

        Optional<QualityReading> findFirstByLineIdOrderByTsDesc(Long lineId);

    List<QualityReading> findByLineIdAndTsBetweenOrderByTsDesc(Long lineId, LocalDateTime startTs, LocalDateTime endTs);

    @Query("""
            select r.sensor.id, avg(r.ph), avg(r.turbidity), avg(r.conductivity)
            from QualityReading r
            where r.line.id = :lineId
              and r.sensor is not null
              and r.ts between :startTs and :endTs
            group by r.sensor.id
            """)
    List<Object[]> findSensorAveragesForDrift(
            @Param("lineId") Long lineId,
            @Param("startTs") LocalDateTime startTs,
            @Param("endTs") LocalDateTime endTs);
}

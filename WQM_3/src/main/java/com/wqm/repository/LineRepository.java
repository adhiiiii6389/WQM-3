package com.wqm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wqm.entity.Line;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("""
            select distinct l
            from Line l
            join Threshold t on t.line = l
            join QualityReading r on r.line = l
            where r.ts = (
                select max(r2.ts)
                from QualityReading r2
                where r2.line = l
            )
            and (
                r.ph < t.minPh or r.ph > t.maxPh
                or r.turbidity < t.minTurbidity or r.turbidity > t.maxTurbidity
                or r.conductivity < t.minConductivity or r.conductivity > t.maxConductivity
            )
            """)
    List<Line> findLinesOutOfSpec();
}

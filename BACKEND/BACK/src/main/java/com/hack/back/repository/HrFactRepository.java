package com.hack.back.repository;

import com.hack.back.entity.fact.HrFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HrFactRepository extends JpaRepository<HrFact, UUID> {

    /** Average value for a given metric, institution, and period */
    @Query("SELECT AVG(h.value) FROM HrFact h " +
           "WHERE h.institution.institutionId = :institutionId " +
           "AND h.period.periodId = :periodId " +
           "AND h.hrMetric = :hrMetric")
    Double avgValueByInstitutionPeriodAndMetric(@Param("institutionId") UUID institutionId,
                                                @Param("periodId") UUID periodId,
                                                @Param("hrMetric") String hrMetric);
}

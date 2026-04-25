package com.hack.back.repository;

import com.hack.back.entity.fact.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, UUID> {

    /** Total hours assigned for an institution in a period */
    @Query("SELECT COALESCE(SUM(ta.hoursAssigned), 0) FROM TeachingAssignment ta " +
           "WHERE ta.institution.institutionId = :institutionId " +
           "AND ta.period.periodId = :periodId")
    Double sumHoursByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                          @Param("periodId") UUID periodId);

    /** Total hours assigned for an org unit in a period */
    @Query("SELECT COALESCE(SUM(ta.hoursAssigned), 0) FROM TeachingAssignment ta " +
           "WHERE ta.orgUnit.orgUnitId = :orgUnitId " +
           "AND ta.period.periodId = :periodId")
    Double sumHoursByOrgUnitAndPeriod(@Param("orgUnitId") UUID orgUnitId,
                                      @Param("periodId") UUID periodId);
}

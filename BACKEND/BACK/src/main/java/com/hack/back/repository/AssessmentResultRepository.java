package com.hack.back.repository;

import com.hack.back.entity.fact.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, UUID> {

    /** Count total assessments for an institution in a period */
    @Query("SELECT COUNT(ar) FROM AssessmentResult ar " +
           "WHERE ar.courseOffering.institution.institutionId = :institutionId " +
           "AND ar.period.periodId = :periodId")
    long countByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                     @Param("periodId") UUID periodId);

    /** Count passed assessments for an institution in a period */
    @Query("SELECT COUNT(ar) FROM AssessmentResult ar " +
           "WHERE ar.courseOffering.institution.institutionId = :institutionId " +
           "AND ar.period.periodId = :periodId AND ar.passed = true")
    long countPassedByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                           @Param("periodId") UUID periodId);

    /** Average score for an institution in a period */
    @Query("SELECT AVG(ar.score) FROM AssessmentResult ar " +
           "WHERE ar.courseOffering.institution.institutionId = :institutionId " +
           "AND ar.period.periodId = :periodId")
    Double avgScoreByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                          @Param("periodId") UUID periodId);
}

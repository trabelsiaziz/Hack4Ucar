package com.hack.back.repository;

import com.hack.back.entity.fact.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /** Total enrolled students for an institution in a period */
    @Query("SELECT COUNT(e) FROM Enrollment e " +
           "WHERE e.institution.institutionId = :institutionId " +
           "AND e.period.periodId = :periodId")
    long countByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                     @Param("periodId") UUID periodId);

    /** Dropped students for an institution in a period */
    @Query("SELECT COUNT(e) FROM Enrollment e " +
           "WHERE e.institution.institutionId = :institutionId " +
           "AND e.period.periodId = :periodId " +
           "AND e.enrollmentStatus = 'dropped'")
    long countDroppedByInstitutionAndPeriod(@Param("institutionId") UUID institutionId,
                                            @Param("periodId") UUID periodId);
}

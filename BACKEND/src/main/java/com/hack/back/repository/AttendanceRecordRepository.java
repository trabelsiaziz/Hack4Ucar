package com.hack.back.repository;

import com.hack.back.entity.fact.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    /**
     * Average attendance_rate for STUDENT records in an institution and period.
     * subjectType discriminates students vs teachers vs staff.
     */
    @Query("SELECT AVG(ar.attendanceRate) FROM AttendanceRecord ar " +
           "WHERE ar.institution.institutionId = :institutionId " +
           "AND ar.period.periodId = :periodId " +
           "AND ar.subjectType = :subjectType")
    Double avgAttendanceRateByInstitutionAndPeriodAndType(
            @Param("institutionId") UUID institutionId,
            @Param("periodId") UUID periodId,
            @Param("subjectType") String subjectType);
}

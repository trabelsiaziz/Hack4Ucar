package com.hack.back.entity.fact;

import com.hack.back.entity.domain.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attendance_id", updatable = false, nullable = false)
    private UUID attendanceId;

    /**
     * Polymorphic discriminator: STUDENT | TEACHER | STAFF
     * Used alongside subjectId to resolve the referenced entity.
     */
    @Column(name = "subject_type", nullable = false)
    private String subjectType;

    /**
     * Polymorphic FK — not DB-enforced.
     * Resolved at application level using subjectType:
     *   STUDENT -> students.student_id
     *   TEACHER -> teachers.teacher_id
     *   STAFF   -> staff_members.staff_id
     */
    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    /** nullable if event-based */
    @Column(name = "attendance_rate")
    private Double attendanceRate;

    /** nullable if rate-based */
    @Column(name = "attendance_value")
    private Double attendanceValue;

    /** nullable if aggregated */
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** attendance_records.course_offering_id -> course_offerings.course_offering_id (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    /** attendance_records.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** attendance_records.org_unit_id -> org_units.org_unit_id (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    /** attendance_records.period_id -> periods.period_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

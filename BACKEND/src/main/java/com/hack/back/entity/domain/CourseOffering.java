package com.hack.back.entity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course_offerings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "course_offering_id", updatable = false, nullable = false)
    private UUID courseOfferingId;

    @Column(name = "section_code")
    private String sectionCode;

    /** onsite | online | hybrid */
    @Column(name = "delivery_mode")
    private String deliveryMode;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** course_offerings.course_id -> courses.course_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** course_offerings.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** course_offerings.org_unit_id -> org_units.org_unit_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    /** course_offerings.period_id -> periods.period_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    /** attendance_records.course_offering_id -> course_offerings.course_offering_id (nullable) */
    @JsonIgnore
    @OneToMany(mappedBy = "courseOffering", fetch = FetchType.LAZY)
    private List<com.hack.back.entity.fact.AttendanceRecord> attendanceRecords;

    /** assessment_results.course_offering_id -> course_offerings.course_offering_id */
    @JsonIgnore
    @OneToMany(mappedBy = "courseOffering", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.hack.back.entity.fact.AssessmentResult> assessmentResults;

    /** teaching_assignments.course_offering_id -> course_offerings.course_offering_id */
    @JsonIgnore
    @OneToMany(mappedBy = "courseOffering", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.hack.back.entity.fact.TeachingAssignment> teachingAssignments;

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

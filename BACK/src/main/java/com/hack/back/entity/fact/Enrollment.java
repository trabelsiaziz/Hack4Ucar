package com.hack.back.entity.fact;

import com.hack.back.entity.domain.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "enrollment_id", updatable = false, nullable = false)
    private UUID enrollmentId;

    @Column(name = "enrollment_status")
    private String enrollmentStatus;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** enrollments.student_id -> students.student_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** enrollments.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** enrollments.program_id -> programs.program_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    /** enrollments.period_id -> periods.period_id */
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

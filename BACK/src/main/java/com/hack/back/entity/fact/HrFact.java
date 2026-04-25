package com.hack.back.entity.fact;

import com.hack.back.entity.domain.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hr_facts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrFact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hr_fact_id", updatable = false, nullable = false)
    private UUID hrFactId;

    /** teachers | admin_staff | all_staff */
    @Column(name = "subject_group")
    private String subjectGroup;

    /** absenteeism_rate | headcount | training_completed | teaching_load */
    @Column(name = "hr_metric")
    private String hrMetric;

    @Column(name = "value")
    private Double value;

    @Column(name = "unit")
    private String unit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** hr_facts.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** hr_facts.org_unit_id -> org_units.org_unit_id (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    /** hr_facts.period_id -> periods.period_id */
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

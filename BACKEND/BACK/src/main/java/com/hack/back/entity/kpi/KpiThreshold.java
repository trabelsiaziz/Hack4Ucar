package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L5 — KPI Governance.
 * DB table: kpi_thresholds
 *
 * DB links:
 *   kpi_thresholds.kpi_id         -> kpi_definitions.kpi_id
 *   kpi_thresholds.institution_id -> institutions.institution_id (nullable)
 *   kpi_thresholds.org_unit_id    -> org_units.org_unit_id (nullable)
 *   kpi_thresholds.period_id      -> periods.period_id (nullable)
 */
@Entity
@Table(name = "kpi_thresholds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "threshold_id", updatable = false, nullable = false)
    private UUID thresholdId;

    @Column(name = "kpi_id", nullable = false)
    private UUID kpiId;

    /** nullable */
    @Column(name = "institution_id")
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    /** nullable */
    @Column(name = "period_id")
    private UUID periodId;

    @Column(name = "warning_threshold")
    private Double warningThreshold;

    @Column(name = "critical_threshold")
    private Double criticalThreshold;

    /** nullable */
    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

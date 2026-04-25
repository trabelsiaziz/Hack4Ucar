package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L6 — KPI Result / Monitoring.
 * DB table: kpi_observations
 *
 * DB links:
 *   kpi_observations.kpi_id         -> kpi_definitions.kpi_id
 *   kpi_observations.tenant_id      -> tenants.tenant_id
 *   kpi_observations.institution_id -> institutions.institution_id
 *   kpi_observations.org_unit_id    -> org_units.org_unit_id (nullable)
 *   kpi_observations.period_id      -> periods.period_id
 *   alerts.observation_id           -> kpi_observations.observation_id
 *   decision_explanations.target_object_id -> kpi_observations.observation_id
 *                                    when target_object_type = KPI_OBSERVATION
 */
@Entity
@Table(name = "kpi_observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "observation_id", updatable = false, nullable = false)
    private UUID observationId;

    @Column(name = "kpi_id", nullable = false)
    private UUID kpiId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    /** institution | org_unit | tenant */
    @Column(name = "scope_type")
    private String scopeType;

    @Column(name = "scope_id")
    private UUID scopeId;

    @Column(name = "computed_value")
    private Double computedValue;

    @Column(name = "unit")
    private String unit;

    /** nullable */
    @Column(name = "baseline_value")
    private Double baselineValue;

    /** up | down | stable */
    @Column(name = "trend_direction")
    private String trendDirection;

    /** draft | final | superseded */
    @Column(name = "status")
    private String status;

    @Column(name = "calculation_run_id")
    private UUID calculationRunId;

    @Column(name = "computed_at")
    private LocalDateTime computedAt;

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

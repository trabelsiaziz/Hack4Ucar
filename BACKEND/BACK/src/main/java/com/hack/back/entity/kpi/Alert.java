package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L6 — KPI Result / Monitoring.
 * DB table: alerts
 *
 * DB links:
 *   alerts.observation_id -> kpi_observations.observation_id (nullable but recommended)
 *   alerts.kpi_id         -> kpi_definitions.kpi_id
 *   alerts.tenant_id      -> tenants.tenant_id
 *   alerts.institution_id -> institutions.institution_id
 *   alerts.org_unit_id    -> org_units.org_unit_id (nullable)
 *   alerts.period_id      -> periods.period_id
 */
@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "alert_id", updatable = false, nullable = false)
    private UUID alertId;

    /** nullable but recommended */
    @Column(name = "observation_id")
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

    /** threshold_breach | trend_deterioration | missing_data */
    @Column(name = "alert_type")
    private String alertType;

    /** low | medium | high | critical */
    @Column(name = "severity")
    private String severity;

    @Column(name = "observed_value")
    private Double observedValue;

    @Column(name = "threshold_value")
    private Double thresholdValue;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /** open | acknowledged | closed */
    @Column(name = "status")
    private String status;

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

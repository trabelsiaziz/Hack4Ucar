package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L6 — KPI Result / Monitoring.
 * DB table: anomalies
 *
 * DB links:
 *   anomalies.tenant_id      -> tenants.tenant_id
 *   anomalies.institution_id -> institutions.institution_id
 *   anomalies.org_unit_id    -> org_units.org_unit_id (nullable)
 *   anomalies.period_id      -> periods.period_id
 */
@Entity
@Table(name = "anomalies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "anomaly_id", updatable = false, nullable = false)
    private UUID anomalyId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    @Column(name = "attribute_name")
    private String attributeName;

    /** statistical_outlier | drift | sudden_change */
    @Column(name = "anomaly_type")
    private String anomalyType;

    @Column(name = "expected_value")
    private Double expectedValue;

    @Column(name = "actual_value")
    private Double actualValue;

    @Column(name = "deviation_score")
    private Double deviationScore;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "detection_method")
    private String detectionMethod;

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

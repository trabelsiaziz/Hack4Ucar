package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L5 — KPI Governance.
 * DB table: kpi_definitions
 *
 * DB links:
 *   kpi_definitions.tenant_id          -> tenants.tenant_id
 *   kpi_formula_versions.kpi_id        -> kpi_definitions.kpi_id
 *   kpi_thresholds.kpi_id              -> kpi_definitions.kpi_id
 *   kpi_observations.kpi_id            -> kpi_definitions.kpi_id
 *   alerts.kpi_id                      -> kpi_definitions.kpi_id
 */
@Entity
@Table(name = "kpi_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "kpi_id", updatable = false, nullable = false)
    private UUID kpiId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "kpi_code", nullable = false, unique = true)
    private String kpiCode;

    @Column(name = "kpi_name", nullable = false)
    private String kpiName;

    /** academic | finance | hr */
    @Column(name = "domain_code")
    private String domainCode;

    @Column(name = "process_code")
    private String processCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit")
    private String unit;

    @Column(name = "value_type")
    private String valueType;

    /** sum | avg | count | ratio | derived */
    @Column(name = "aggregation_type")
    private String aggregationType;

    /** higher_is_better | lower_is_better | target_is_best */
    @Column(name = "direction")
    private String direction;

    /** institution | org_unit | tenant */
    @Column(name = "scope_type_default")
    private String scopeTypeDefault;

    /** program | institution | staff_group */
    @Column(name = "entity_type_default")
    private String entityTypeDefault;

    /** monthly | quarterly | semester | yearly */
    @Column(name = "calculation_frequency")
    private String calculationFrequency;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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

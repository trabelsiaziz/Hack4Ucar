package com.hack.back.entity.kpi;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L5 — KPI Governance.
 * DB table: kpi_formula_versions
 *
 * DB links:
 *   kpi_formula_versions.kpi_id -> kpi_definitions.kpi_id
 */
@Entity
@Table(name = "kpi_formula_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiFormulaVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "formula_version_id", updatable = false, nullable = false)
    private UUID formulaVersionId;

    /** kpi_formula_versions.kpi_id -> kpi_definitions.kpi_id */
    @Column(name = "kpi_id", nullable = false)
    private UUID kpiId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "formula_expression", columnDefinition = "TEXT", nullable = false)
    private String formulaExpression;

    /** sql | java | python | rules | hybrid */
    @Column(name = "logic_type")
    private String logicType;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** nullable */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent;

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

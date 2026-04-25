package com.hack.back.entity.canonical;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L4 — Canonical cross-service pivot record.
 * Maps to DB tables: business_records (header) + business_record_values (value payload).
 * Both are stored here as a single flattened entity for simplicity.
 *
 * DB links:
 *   business_records.tenant_id      -> tenants.tenant_id
 *   business_records.institution_id -> institutions.institution_id
 *   business_records.org_unit_id    -> org_units.org_unit_id (nullable)
 *   business_records.period_id      -> periods.period_id
 *   business_records.source_id      -> source_artifacts.source_id
 *   business_record_values.record_id -> business_records.record_id
 */
@Entity
@Table(name = "business_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRecord {

    // ── Header fields ──────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id", updatable = false, nullable = false)
    private UUID recordId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    /** academic | finance | hr */
    @Column(name = "domain_code", nullable = false)
    private String domainCode;

    /** exams | attendance | budget | staffing | enrollment */
    @Column(name = "process_code", nullable = false)
    private String processCode;

    /** student | teacher | staff_member | program | institution | org_unit */
    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    /** success_rate | consumed_budget | absenteeism_rate | etc. */
    @Column(name = "attribute_name")
    private String attributeName;

    /** draft | validated | rejected | final */
    @Column(name = "record_status")
    private String recordStatus;

    /** source_artifacts.source_id */
    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "schema_version")
    private String schemaVersion;

    @Column(name = "produced_by")
    private String producedBy;

    // ── Value payload (stored in business_record_values, denormalised here) ──

    /** numeric | text | boolean | date */
    @Column(name = "value_type")
    private String valueType;

    @Column(name = "value_numeric")
    private Double valueNumeric;

    @Column(name = "value_text", columnDefinition = "TEXT")
    private String valueText;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "unit")
    private String unit;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    // ── Audit ──────────────────────────────────────────────────────────────

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

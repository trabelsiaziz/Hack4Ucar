package com.hack.back.entity.ai;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L7 — AI Analysis.
 * DB table: insights
 *
 * DB links:
 *   insights.tenant_id      -> tenants.tenant_id
 *   insights.institution_id -> institutions.institution_id
 *   insights.org_unit_id    -> org_units.org_unit_id (nullable)
 *   insights.period_id      -> periods.period_id
 *   decision_explanations.target_object_id -> insights.insight_id
 *                            when target_object_type = INSIGHT
 */
@Entity
@Table(name = "insights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "insight_id", updatable = false, nullable = false)
    private UUID insightId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    /** executive_summary | comparative_analysis | risk_summary */
    @Column(name = "insight_type")
    private String insightType;

    @Column(name = "title")
    private String title;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    /** ai-engine | hybrid-engine */
    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "confidence_score")
    private Double confidenceScore;

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

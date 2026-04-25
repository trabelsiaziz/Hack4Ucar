package com.hack.back.entity.ai;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L7 — AI Analysis.
 * DB table: recommendations
 *
 * DB links:
 *   recommendations.tenant_id      -> tenants.tenant_id
 *   recommendations.institution_id -> institutions.institution_id
 *   recommendations.org_unit_id    -> org_units.org_unit_id (nullable)
 *   recommendations.period_id      -> periods.period_id
 *   decision_explanations.target_object_id -> recommendations.recommendation_id
 *                           when target_object_type = RECOMMENDATION
 */
@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "recommendation_id", updatable = false, nullable = false)
    private UUID recommendationId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "institution_id", nullable = false)
    private UUID institutionId;

    /** nullable */
    @Column(name = "org_unit_id")
    private UUID orgUnitId;

    @Column(name = "period_id", nullable = false)
    private UUID periodId;

    /** strategic_action | operational_action | data_quality_action */
    @Column(name = "category")
    private String category;

    /** low | medium | high | urgent */
    @Column(name = "priority")
    private String priority;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    /** rules-engine | ai-engine | hybrid-engine */
    @Column(name = "generated_by")
    private String generatedBy;

    /** proposed | accepted | rejected | completed */
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

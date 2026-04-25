package com.hack.back.entity.ai;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * L7 — AI Analysis.
 * DB table: decision_explanations
 *
 * DB links (polymorphic — resolved at application level):
 *   decision_explanations.target_object_id can reference:
 *     - kpi_observations.observation_id  when target_object_type = KPI_OBSERVATION
 *     - alerts.alert_id                  when target_object_type = ALERT
 *     - anomalies.anomaly_id             when target_object_type = ANOMALY
 *     - insights.insight_id              when target_object_type = INSIGHT
 *     - recommendations.recommendation_id when target_object_type = RECOMMENDATION
 */
@Entity
@Table(name = "decision_explanations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "explanation_id", updatable = false, nullable = false)
    private UUID explanationId;

    /**
     * Polymorphic discriminator.
     * KPI_OBSERVATION | ALERT | ANOMALY | INSIGHT | RECOMMENDATION
     */
    @Column(name = "target_object_type", nullable = false)
    private String targetObjectType;

    /**
     * Polymorphic FK — not enforced at DB level due to polymorphism.
     * Resolved at application level using targetObjectType.
     */
    @Column(name = "target_object_id", nullable = false)
    private UUID targetObjectId;

    /** threshold | statistical | rule | llm_summary */
    @Column(name = "explanation_type")
    private String explanationType;

    @Column(name = "explanation_text", columnDefinition = "TEXT")
    private String explanationText;

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

package com.hack.back.entity.fact;

import com.hack.back.entity.domain.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "budget_facts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetFact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "budget_fact_id", updatable = false, nullable = false)
    private UUID budgetFactId;

    /** allocated | consumed | committed */
    @Column(name = "budget_type")
    private String budgetType;

    @Column(name = "budget_category")
    private String budgetCategory;

    @Column(name = "amount", precision = 20, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** budget_facts.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** budget_facts.org_unit_id -> org_units.org_unit_id (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    /** budget_facts.period_id -> periods.period_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    // ── Lifecycle ──────────────────────────────────────────────────────────

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

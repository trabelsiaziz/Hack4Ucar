package com.hack.back.entity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "periods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "period_id", updatable = false, nullable = false)
    private UUID periodId;

    @Column(name = "period_code", nullable = false)
    private String periodCode;

    /** month | quarter | semester | year */
    @Column(name = "period_type")
    private String periodType;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_closed")
    private Boolean isClosed;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** periods.tenant_id -> tenants.tenant_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** course_offerings.period_id -> periods.period_id */
    @JsonIgnore
    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseOffering> courseOfferings;

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

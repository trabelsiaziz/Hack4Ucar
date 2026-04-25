package com.hack.back.entity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "org_units")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "org_unit_id", updatable = false, nullable = false)
    private UUID orgUnitId;

    @Column(name = "org_unit_code", nullable = false)
    private String orgUnitCode;

    @Column(name = "org_unit_name", nullable = false)
    private String orgUnitName;

    /** faculty | department | service | lab */
    @Column(name = "org_unit_type")
    private String orgUnitType;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** org_units.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** org_units.parent_org_unit_id -> org_units.org_unit_id (self-ref, nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_org_unit_id")
    private OrgUnit parentOrgUnit;

    /** children org units */
    @JsonIgnore
    @OneToMany(mappedBy = "parentOrgUnit", fetch = FetchType.LAZY)
    private List<OrgUnit> childOrgUnits;

    /** users.org_unit_id -> org_units.org_unit_id (nullable) */
    @JsonIgnore
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<UserAccount> users;

    /** teachers.org_unit_id -> org_units.org_unit_id */
    @JsonIgnore
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<Teacher> teachers;

    /** staff_members.org_unit_id -> org_units.org_unit_id */
    @JsonIgnore
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<StaffMember> staffMembers;

    /** programs.org_unit_id -> org_units.org_unit_id */
    @JsonIgnore
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<Program> programs;

    /** course_offerings.org_unit_id -> org_units.org_unit_id */
    @JsonIgnore
    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
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

package com.hack.back.entity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "institutions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "institution_id", updatable = false, nullable = false)
    private UUID institutionId;

    @Column(name = "institution_code", nullable = false, unique = true)
    private String institutionCode;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    /** faculty | department | service | lab */
    @Column(name = "institution_type")
    private String institutionType;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** institutions.tenant_id -> tenants.tenant_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** org_units.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrgUnit> orgUnits;

    /** students.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> students;

    /** teachers.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Teacher> teachers;

    /** staff_members.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StaffMember> staffMembers;

    /** programs.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Program> programs;

    /** course_offerings.institution_id -> institutions.institution_id */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseOffering> courseOfferings;

    /** users.institution_id -> institutions.institution_id (nullable) */
    @JsonIgnore
    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    private List<UserAccount> users;

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

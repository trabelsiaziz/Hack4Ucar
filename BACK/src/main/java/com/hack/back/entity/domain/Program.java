package com.hack.back.entity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "program_id", updatable = false, nullable = false)
    private UUID programId;

    @Column(name = "program_code", nullable = false, unique = true)
    private String programCode;

    @Column(name = "program_name", nullable = false)
    private String programName;

    @Column(name = "degree_level")
    private String degreeLevel;

    @Column(name = "duration_years")
    private Integer durationYears;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationships ──────────────────────────────────────────────────────

    /** programs.institution_id -> institutions.institution_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    /** programs.org_unit_id -> org_units.org_unit_id */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    /** courses.program_id -> programs.program_id */
    @JsonIgnore
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    /** students.current_program_id -> programs.program_id */
    @JsonIgnore
    @OneToMany(mappedBy = "currentProgram", fetch = FetchType.LAZY)
    private List<Student> students;

    /** enrollments.program_id -> programs.program_id */
    @JsonIgnore
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.hack.back.entity.fact.Enrollment> enrollments;

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

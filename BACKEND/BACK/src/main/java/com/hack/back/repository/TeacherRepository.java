package com.hack.back.repository;

import com.hack.back.entity.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    Optional<Teacher> findByTeacherCode(String teacherCode);

    List<Teacher> findByEmploymentStatus(String employmentStatus);

    List<Teacher> findByInstitution_InstitutionId(UUID institutionId);

    boolean existsByTeacherCode(String teacherCode);

    long countByInstitution_InstitutionIdAndEmploymentStatus(UUID institutionId, String employmentStatus);
}

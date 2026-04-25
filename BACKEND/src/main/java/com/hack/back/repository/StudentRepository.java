package com.hack.back.repository;

import com.hack.back.entity.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByStudentCode(String studentCode);

    List<Student> findByEnrollmentStatus(String enrollmentStatus);

    List<Student> findByInstitution_InstitutionId(UUID institutionId);

    boolean existsByStudentCode(String studentCode);
}

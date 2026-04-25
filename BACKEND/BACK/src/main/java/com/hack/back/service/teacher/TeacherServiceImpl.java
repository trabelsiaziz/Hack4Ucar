package com.hack.back.service.teacher;

import com.hack.back.entity.domain.Teacher;
import com.hack.back.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link TeacherService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    // ── Create ────────────────────────────────────────────────────────────

    @Override
    public Teacher create(Teacher teacher) {
        if (teacherRepository.existsByTeacherCode(teacher.getTeacherCode())) {
            throw new IllegalArgumentException(
                    "Teacher with code '" + teacher.getTeacherCode() + "' already exists.");
        }
        return teacherRepository.save(teacher);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Teacher findById(UUID teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teacher not found with id: " + teacherId));
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findByTeacherCode(String teacherCode) {
        return teacherRepository.findByTeacherCode(teacherCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teacher not found with code: " + teacherCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByInstitution(UUID institutionId) {
        return teacherRepository.findByInstitution_InstitutionId(institutionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByEmploymentStatus(String status) {
        return teacherRepository.findByEmploymentStatus(status);
    }

    // ── Update ────────────────────────────────────────────────────────────

    @Override
    public Teacher update(UUID teacherId, Teacher updated) {
        Teacher existing = findById(teacherId);

        existing.setTeacherCode(updated.getTeacherCode());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setGradeOrRank(updated.getGradeOrRank());
        existing.setSpecialization(updated.getSpecialization());
        existing.setHireDate(updated.getHireDate());
        existing.setContractType(updated.getContractType());
        existing.setEmploymentStatus(updated.getEmploymentStatus());
        existing.setInstitution(updated.getInstitution());
        existing.setOrgUnit(updated.getOrgUnit());

        return teacherRepository.save(existing);
    }

    // ── Delete ────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new EntityNotFoundException("Teacher not found with id: " + teacherId);
        }
        teacherRepository.deleteById(teacherId);
    }
}

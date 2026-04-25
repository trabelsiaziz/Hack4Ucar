package com.hack.back.service.student;

import com.hack.back.entity.domain.Student;
import com.hack.back.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link StudentService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // ── Create ────────────────────────────────────────────────────────────

    @Override
    public Student create(Student student) {
        if (studentRepository.existsByStudentCode(student.getStudentCode())) {
            throw new IllegalArgumentException(
                    "Student with code '" + student.getStudentCode() + "' already exists.");
        }
        return studentRepository.save(student);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Student findById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with id: " + studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Student findByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with code: " + studentCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByInstitution(UUID institutionId) {
        return studentRepository.findByInstitution_InstitutionId(institutionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByEnrollmentStatus(String status) {
        return studentRepository.findByEnrollmentStatus(status);
    }

    // ── Update ────────────────────────────────────────────────────────────

    @Override
    public Student update(UUID studentId, Student updated) {
        Student existing = findById(studentId);

        existing.setStudentCode(updated.getStudentCode());
        existing.setNationalIdMasked(updated.getNationalIdMasked());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setBirthDate(updated.getBirthDate());
        existing.setGender(updated.getGender());
        existing.setAdmissionDate(updated.getAdmissionDate());
        existing.setGraduationDate(updated.getGraduationDate());
        existing.setEnrollmentStatus(updated.getEnrollmentStatus());
        existing.setInstitution(updated.getInstitution());
        existing.setOrgUnit(updated.getOrgUnit());
        existing.setCurrentProgram(updated.getCurrentProgram());

        return studentRepository.save(existing);
    }

    // ── Delete ────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student not found with id: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }
}

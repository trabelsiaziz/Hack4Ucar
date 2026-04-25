package com.hack.back.service.student;

import com.hack.back.entity.domain.Student;

import java.util.List;
import java.util.UUID;

/**
 * CRUD service contract for {@link Student}.
 */
public interface StudentService {

    /** Create a new student record. */
    Student create(Student student);

    /** Retrieve a student by its primary key. */
    Student findById(UUID studentId);

    /** Retrieve a student by its human-readable code. */
    Student findByStudentCode(String studentCode);

    /** Retrieve all students. */
    List<Student> findAll();

    /** Retrieve all students belonging to a given institution. */
    List<Student> findByInstitution(UUID institutionId);

    /** Retrieve all students filtered by enrollment status. */
    List<Student> findByEnrollmentStatus(String status);

    /** Full update of an existing student (all fields). */
    Student update(UUID studentId, Student updated);

    /** Delete a student by its primary key. */
    void delete(UUID studentId);
}

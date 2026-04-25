package com.hack.back.service.teacher;

import com.hack.back.entity.domain.Teacher;

import java.util.List;
import java.util.UUID;

/**
 * CRUD service contract for {@link Teacher}.
 */
public interface TeacherService {

    /** Create a new teacher record. */
    Teacher create(Teacher teacher);

    /** Retrieve a teacher by its primary key. */
    Teacher findById(UUID teacherId);

    /** Retrieve a teacher by its human-readable code. */
    Teacher findByTeacherCode(String teacherCode);

    /** Retrieve all teachers. */
    List<Teacher> findAll();

    /** Retrieve all teachers belonging to a given institution. */
    List<Teacher> findByInstitution(UUID institutionId);

    /** Retrieve all teachers filtered by employment status. */
    List<Teacher> findByEmploymentStatus(String status);

    /** Full update of an existing teacher (all fields). */
    Teacher update(UUID teacherId, Teacher updated);

    /** Delete a teacher by its primary key. */
    void delete(UUID teacherId);
}

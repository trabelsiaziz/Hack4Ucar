package com.hack.back.application;

import com.hack.back.entity.domain.Student;
import com.hack.back.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing CRUD endpoints for {@link Student}.
 *
 * Base path: /api/v1/students
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ── POST /api/v1/students ─────────────────────────────────────────────

    /**
     * Create a new student.
     *
     * @param student request body
     * @return 201 Created with the persisted student
     */
    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        Student created = studentService.create(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── GET /api/v1/students ──────────────────────────────────────────────

    /**
     * Retrieve all students, optionally filtered by enrollment status or institution.
     *
     * @param status        (optional) filter by enrollment status
     * @param institutionId (optional) filter by institution UUID
     * @return 200 OK with list of students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID institutionId) {

        List<Student> students;

        if (status != null) {
            students = studentService.findByEnrollmentStatus(status);
        } else if (institutionId != null) {
            students = studentService.findByInstitution(institutionId);
        } else {
            students = studentService.findAll();
        }

        return ResponseEntity.ok(students);
    }

    // ── GET /api/v1/students/{id} ─────────────────────────────────────────

    /**
     * Retrieve a student by UUID.
     *
     * @param id student UUID
     * @return 200 OK with the student
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    // ── GET /api/v1/students/code/{studentCode} ───────────────────────────

    /**
     * Retrieve a student by its human-readable student code.
     *
     * @param studentCode e.g. "STU-2024-001"
     * @return 200 OK with the student
     */
    @GetMapping("/code/{studentCode}")
    public ResponseEntity<Student> getByCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(studentService.findByStudentCode(studentCode));
    }

    // ── PUT /api/v1/students/{id} ─────────────────────────────────────────

    /**
     * Full update of a student record.
     *
     * @param id      student UUID
     * @param student request body with updated fields
     * @return 200 OK with the updated student
     */
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(
            @PathVariable UUID id,
            @RequestBody Student student) {
        return ResponseEntity.ok(studentService.update(id, student));
    }

    // ── DELETE /api/v1/students/{id} ──────────────────────────────────────

    /**
     * Delete a student by UUID.
     *
     * @param id student UUID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

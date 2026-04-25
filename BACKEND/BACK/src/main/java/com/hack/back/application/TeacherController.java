package com.hack.back.application;

import com.hack.back.entity.domain.Teacher;
import com.hack.back.service.teacher.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing CRUD endpoints for {@link Teacher}.
 *
 * Base path: /api/v1/teachers
 */
@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // ── POST /api/v1/teachers ─────────────────────────────────────────────

    /**
     * Create a new teacher.
     *
     * @param teacher request body
     * @return 201 Created with the persisted teacher
     */
    @PostMapping
    public ResponseEntity<Teacher> create(@RequestBody Teacher teacher) {
        Teacher created = teacherService.create(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── GET /api/v1/teachers ──────────────────────────────────────────────

    /**
     * Retrieve all teachers, optionally filtered by employment status or institution.
     *
     * @param status        (optional) filter by employment status
     * @param institutionId (optional) filter by institution UUID
     * @return 200 OK with list of teachers
     */
    @GetMapping
    public ResponseEntity<List<Teacher>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID institutionId) {

        List<Teacher> teachers;

        if (status != null) {
            teachers = teacherService.findByEmploymentStatus(status);
        } else if (institutionId != null) {
            teachers = teacherService.findByInstitution(institutionId);
        } else {
            teachers = teacherService.findAll();
        }

        return ResponseEntity.ok(teachers);
    }

    // ── GET /api/v1/teachers/{id} ─────────────────────────────────────────

    /**
     * Retrieve a teacher by UUID.
     *
     * @param id teacher UUID
     * @return 200 OK with the teacher
     */
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    // ── GET /api/v1/teachers/code/{teacherCode} ───────────────────────────

    /**
     * Retrieve a teacher by its human-readable teacher code.
     *
     * @param teacherCode e.g. "TCH-2024-001"
     * @return 200 OK with the teacher
     */
    @GetMapping("/code/{teacherCode}")
    public ResponseEntity<Teacher> getByCode(@PathVariable String teacherCode) {
        return ResponseEntity.ok(teacherService.findByTeacherCode(teacherCode));
    }

    // ── PUT /api/v1/teachers/{id} ─────────────────────────────────────────

    /**
     * Full update of a teacher record.
     *
     * @param id      teacher UUID
     * @param teacher request body with updated fields
     * @return 200 OK with the updated teacher
     */
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(
            @PathVariable UUID id,
            @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.update(id, teacher));
    }

    // ── DELETE /api/v1/teachers/{id} ──────────────────────────────────────

    /**
     * Delete a teacher by UUID.
     *
     * @param id teacher UUID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.iqac.project.controller;

import com.iqac.project.dto.TeachingScheduleDTO;
import com.iqac.project.entity.TeachingSchedule;
import com.iqac.project.service.TeachingScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/teaching-schedule")
public class TeachingScheduleController {

    private final TeachingScheduleService teachingScheduleService;

    public TeachingScheduleController(TeachingScheduleService teachingScheduleService) {
        this.teachingScheduleService = teachingScheduleService;
    }

    @GetMapping
    public ResponseEntity<List<TeachingSchedule>> getAll(Authentication auth) {
        return ResponseEntity.ok(teachingScheduleService.getAll(getDeptId(auth)));
    }

    @GetMapping("/year/{academicYear}")
    public ResponseEntity<List<TeachingSchedule>> getByYear(@PathVariable String academicYear, Authentication auth) {
        return ResponseEntity.ok(teachingScheduleService.getByAcademicYear(getDeptId(auth), academicYear));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeachingSchedule> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(teachingScheduleService.getById(id, getDeptId(auth)));
    }

    @PostMapping
    public ResponseEntity<TeachingSchedule> create(@RequestBody TeachingScheduleDTO dto, Authentication auth) {
        return ResponseEntity.ok(teachingScheduleService.create(dto, getDeptId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeachingSchedule> update(@PathVariable Long id, @RequestBody TeachingScheduleDTO dto, Authentication auth) {
        return ResponseEntity.ok(teachingScheduleService.update(id, dto, getDeptId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        teachingScheduleService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }

    private Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }
}

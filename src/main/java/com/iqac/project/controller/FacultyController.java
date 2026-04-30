package com.iqac.project.controller;

import com.iqac.project.dto.FacultyDTO;
import com.iqac.project.entity.Faculty;
import com.iqac.project.service.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getAll(Authentication auth) {
        return ResponseEntity.ok(facultyService.getAll(getDeptId(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(facultyService.getById(id, getDeptId(auth)));
    }

    @PostMapping
    public ResponseEntity<Faculty> create(@RequestBody FacultyDTO dto, Authentication auth) {
        return ResponseEntity.ok(facultyService.create(dto, getDeptId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> update(@PathVariable Long id, @RequestBody FacultyDTO dto, Authentication auth) {
        return ResponseEntity.ok(facultyService.update(id, dto, getDeptId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        facultyService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Faculty deleted successfully");
    }

    private Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }
}

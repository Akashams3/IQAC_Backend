package com.iqac.project.controller;

import com.iqac.project.dto.DepartmentDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<Department> create(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<Department> update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok("Department deleted successfully");
    }
}

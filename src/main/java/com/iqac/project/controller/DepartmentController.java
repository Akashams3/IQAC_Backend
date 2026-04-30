package com.iqac.project.controller;

import com.iqac.project.dto.DepartmentDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.service.DepartmentService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Department> create(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.create(dto));
    }
}

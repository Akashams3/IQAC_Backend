package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.ClassMentorRequest;
import com.iqac.project.dto.ClassMentorResponse;
import com.iqac.project.service.ClassMentorService;
import com.iqac.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/academics/planning/mentor")
public class ClassMentorController {

    private final ClassMentorService classMentorService;
    private final AuthUtil authUtil;

    public ClassMentorController(ClassMentorService classMentorService, AuthUtil authUtil) {
        this.classMentorService = classMentorService;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ClassMentorRequest req, Authentication auth) {
        classMentorService.create(authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        List<ClassMentorResponse> data = classMentorService.getAll(authUtil.getDeptId(auth), academicYear);
        return ResponseEntity.ok(ApiResponse.of("Success", data));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", classMentorService.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ClassMentorRequest req, Authentication auth) {
        classMentorService.update(id, authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteByYear(@RequestParam String academicYear, Authentication auth) {
        classMentorService.deleteByYear(authUtil.getDeptId(auth), academicYear);
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        classMentorService.delete(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

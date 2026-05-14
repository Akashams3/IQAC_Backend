package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.ClassInchargeRequest;
import com.iqac.project.dto.ClassInchargeResponse;
import com.iqac.project.service.ClassInchargeService;
import com.iqac.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/academics/planning/incharge")
public class ClassInchargeController {

    private final ClassInchargeService classInchargeService;
    private final AuthUtil authUtil;

    public ClassInchargeController(ClassInchargeService classInchargeService, AuthUtil authUtil) {
        this.classInchargeService = classInchargeService;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ClassInchargeRequest req, Authentication auth) {
        classInchargeService.create(authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        List<ClassInchargeResponse> data = classInchargeService.getAll(authUtil.getDeptId(auth), academicYear);
        return ResponseEntity.ok(ApiResponse.of("Success", data));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", classInchargeService.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody ClassInchargeRequest req, Authentication auth) {
        classInchargeService.update(id, authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteByYear(@RequestParam String academicYear, Authentication auth) {
        classInchargeService.deleteByYear(authUtil.getDeptId(auth), academicYear);
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        classInchargeService.delete(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

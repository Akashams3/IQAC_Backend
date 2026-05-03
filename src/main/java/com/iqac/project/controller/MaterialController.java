package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.MaterialRequest;
import com.iqac.project.service.MaterialService;
import com.iqac.project.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/iqac/academics/planning/materials")
public class MaterialController {

    private final MaterialService service;
    private final AuthUtil authUtil;

    public MaterialController(MaterialService service, AuthUtil authUtil) {
        this.service = service;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute MaterialRequest req,
            Authentication auth) throws IOException {
        service.create(file, authUtil.getFacultyId(auth), authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMy(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", service.getMy(authUtil.getFacultyId(auth))));
    }

    @PreAuthorize("hasAnyRole('HOD','IQAC_COORDINATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String status,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", service.getAll(authUtil.getDeptId(auth), academicYear, semester, status)));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestBody MaterialRequest req,
            Authentication auth) {
        service.update(id, authUtil.getFacultyId(auth), authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse> submit(@PathVariable Long id, Authentication auth) {
        service.submit(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Submitted successfully"));
    }

    @PreAuthorize("hasRole('HOD')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id, Authentication auth) {
        service.approve(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Approved successfully"));
    }

    @PreAuthorize("hasRole('HOD')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Long id, Authentication auth) {
        service.reject(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Rejected successfully"));
    }

    @PreAuthorize("hasAnyRole('HOD','IQAC_COORDINATOR','FACULTY')")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Authentication auth) throws IOException {
        File file = service.download(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .header("Content-Type", "application/pdf")
                .body(Files.readAllBytes(file.toPath()));
    }

    @PreAuthorize("hasAnyRole('FACULTY','IQAC_COORDINATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        service.delete(id, authUtil.getDeptId(auth), role);
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

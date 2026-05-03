package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.entity.EResource;
import com.iqac.project.service.EResourceService;
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
@RequestMapping("/iqac/academics/planning/e-resources")
public class EResourceController {

    private final EResourceService service;
    private final AuthUtil authUtil;

    public EResourceController(EResourceService service, AuthUtil authUtil) {
        this.service = service;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String title,
            @RequestParam String subject,
            @RequestParam String academicYear,
            @RequestParam String className,
            @RequestParam String type,
            @RequestParam(required = false) String link,
            Authentication auth) throws IOException {
        service.create(file, title, subject, academicYear, className, type, link,
                authUtil.getFacultyId(auth), authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMy(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", service.getMy(authUtil.getFacultyId(auth))));
    }

    @PreAuthorize("hasAnyRole('HOD','IQAC_COORDINATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", service.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasAnyRole('HOD','IQAC_COORDINATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success",
                service.getAll(authUtil.getDeptId(auth), academicYear, className, subject, type, status)));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String subject,
            @RequestParam String academicYear,
            @RequestParam String className,
            @RequestParam(required = false) String link,
            Authentication auth) {
        service.update(id, authUtil.getFacultyId(auth), authUtil.getDeptId(auth),
                title, subject, academicYear, className, link);
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

    @PreAuthorize("hasAnyRole('FACULTY','HOD','IQAC_COORDINATOR')")
    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id, Authentication auth) throws IOException {
        EResource r = (EResource) service.getById(id, authUtil.getDeptId(auth));
        if ("LINK".equalsIgnoreCase(r.getType())) {
            if (r.getLink() == null || r.getLink().isBlank())
                return ResponseEntity.ok(ApiResponse.of("No link available"));
            return ResponseEntity.ok(ApiResponse.of("Success", r.getLink()));
        }
        File file = service.download(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .header("Content-Type", "application/octet-stream")
                .body(Files.readAllBytes(file.toPath()));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, authUtil.getFacultyId(auth), authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

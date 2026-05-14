package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.VideoLectureRequest;
import com.iqac.project.service.VideoLectureService;
import com.iqac.project.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iqac/academics/planning/video-lectures")
public class VideoLectureController {

    private final VideoLectureService service;
    private final AuthUtil authUtil;

    public VideoLectureController(VideoLectureService service, AuthUtil authUtil) {
        this.service = service;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody VideoLectureRequest req, Authentication auth) {
        service.create(req, authUtil.getFacultyId(auth), authUtil.getDeptId(auth));
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
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String status,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success",
                service.getAll(authUtil.getDeptId(auth), subject, academicYear, className, status)));
    }

    @PreAuthorize("hasAnyRole('HOD','IQAC_COORDINATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success",
                service.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
            @RequestBody VideoLectureRequest req, Authentication auth) {
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

    @PreAuthorize("hasRole('FACULTY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, authUtil.getFacultyId(auth), authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

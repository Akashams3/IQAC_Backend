package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.LessonPlanRequest;
import com.iqac.project.service.LessonPlanService;
import com.iqac.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iqac/academics/planning/lesson-plan")
public class LessonPlanController {

    private final LessonPlanService lessonPlanService;
    private final AuthUtil authUtil;

    public LessonPlanController(LessonPlanService lessonPlanService, AuthUtil authUtil) {
        this.lessonPlanService = lessonPlanService;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody LessonPlanRequest req, Authentication auth) {
        lessonPlanService.create(authUtil.getFacultyId(auth), authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMy(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", lessonPlanService.getByFaculty(authUtil.getFacultyId(auth))));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", lessonPlanService.getByDept(authUtil.getDeptId(auth), academicYear)));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody LessonPlanRequest req, Authentication auth) {
        lessonPlanService.update(id, authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse> submit(@PathVariable Long id, Authentication auth) {
        lessonPlanService.submit(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Submitted successfully"));
    }

    @PreAuthorize("hasRole('HOD')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Long id, Authentication auth) {
        lessonPlanService.approve(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Approved successfully"));
    }

    @PreAuthorize("hasRole('HOD')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Long id, Authentication auth) {
        lessonPlanService.reject(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Rejected successfully"));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        lessonPlanService.delete(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.dto.CcmRequest;
import com.iqac.project.service.CcmMemberService;
import com.iqac.project.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iqac/academics/planning/ccm")
public class CcmController {

    private final CcmMemberService ccmService;
    private final AuthUtil authUtil;

    public CcmController(CcmMemberService ccmService, AuthUtil authUtil) {
        this.ccmService = ccmService;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse> create(@RequestBody CcmRequest req, Authentication auth) {
        ccmService.create(authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Created successfully"));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String role,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success",
                ccmService.getAll(authUtil.getDeptId(auth), academicYear, className, role)));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/members/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", ccmService.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/members/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody CcmRequest req, Authentication auth) {
        ccmService.update(id, authUtil.getDeptId(auth), req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        ccmService.delete(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

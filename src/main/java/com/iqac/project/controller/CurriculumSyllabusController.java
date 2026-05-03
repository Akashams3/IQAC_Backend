package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.service.CurriculumSyllabusService;
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
@RequestMapping("/iqac/academics/planning/curriculum-syllabus")
public class CurriculumSyllabusController {

    private final CurriculumSyllabusService curriculumSyllabusService;
    private final AuthUtil authUtil;

    public CurriculumSyllabusController(CurriculumSyllabusService curriculumSyllabusService, AuthUtil authUtil) {
        this.curriculumSyllabusService = curriculumSyllabusService;
        this.authUtil = authUtil;
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestParam String regulation,
            Authentication auth) throws IOException {
        String id = curriculumSyllabusService.uploadFile(file, authUtil.getDeptId(auth), academicYear, semester, regulation);
        return ResponseEntity.ok(ApiResponse.of("Uploaded successfully", id));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", curriculumSyllabusService.getAll(authUtil.getDeptId(auth), academicYear)));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.of("Success", curriculumSyllabusService.getById(id, authUtil.getDeptId(auth))));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Authentication auth) throws IOException {
        String filePath = curriculumSyllabusService.getFilePath(id, authUtil.getDeptId(auth));
        File file = curriculumSyllabusService.downloadFile(filePath);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .header("Content-Type", "application/pdf")
                .body(Files.readAllBytes(file.toPath()));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id, Authentication auth) {
        curriculumSyllabusService.delete(id, authUtil.getDeptId(auth));
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

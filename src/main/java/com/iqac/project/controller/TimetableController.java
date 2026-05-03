package com.iqac.project.controller;

import com.iqac.project.dto.ApiResponse;
import com.iqac.project.entity.Timetable;
import com.iqac.project.service.TimetableService;
import com.iqac.project.util.AuthUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/iqac/academics/planning/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final AuthUtil authUtil;
    private final MessageSource messageSource;

    public TimetableController(TimetableService timetableService, AuthUtil authUtil, MessageSource messageSource) {
        this.timetableService = timetableService;
        this.authUtil = authUtil;
        this.messageSource = messageSource;
    }

    private String msg(String key) { return messageSource.getMessage(key, null, LocaleContextHolder.getLocale()); }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester,
            Authentication auth) {
        List<Timetable> data = timetableService.getAll(authUtil.getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok(ApiResponse.of("Success", data));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) throws IOException {
        timetableService.uploadExcel(file, authUtil.getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok(ApiResponse.of(msg("timetable.uploaded")));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/{year}/{semester}/{day}/{period}")
    public ResponseEntity<ApiResponse> updateBySlot(
            @PathVariable String year, @PathVariable String semester,
            @PathVariable String day, @PathVariable String period,
            @RequestBody Timetable req, Authentication auth) {
        timetableService.updateBySlot(authUtil.getDeptId(auth), year, semester, day, period, req);
        return ResponseEntity.ok(ApiResponse.of("Updated successfully"));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) throws IOException {
        byte[] file = timetableService.download(authUtil.getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=timetable.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(file);
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteByFilter(
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) {
        timetableService.deleteByFilter(authUtil.getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok(ApiResponse.of("Deleted successfully"));
    }
}

package com.iqac.project.controller;

import com.iqac.project.dto.ClassInchargeRequest;
import com.iqac.project.dto.ClassInchargeResponse;
import com.iqac.project.entity.ClassIncharge;
import com.iqac.project.entity.Timetable;
import com.iqac.project.service.ClassInchargeService;
import com.iqac.project.service.TimetableService;
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
@RequestMapping("/iqac/academics/planning")
public class AcademicsController {

    private final TimetableService timetableService;
    private final ClassInchargeService classInchargeService;
    private final MessageSource messageSource;

    public AcademicsController(TimetableService timetableService, ClassInchargeService classInchargeService,
                               MessageSource messageSource) {
        this.timetableService = timetableService;
        this.classInchargeService = classInchargeService;
        this.messageSource = messageSource;
    }

    private Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    // ── Timetable ──────────────────────────────────────────────

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/timetable")
    public ResponseEntity<List<Timetable>> getAllTimetable(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester,
            Authentication auth) {
        return ResponseEntity.ok(timetableService.getAll(getDeptId(auth), academicYear, semester));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/timetable/upload")
    public ResponseEntity<String> uploadTimetable(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) throws IOException {

        timetableService.uploadExcel(file, getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok(msg("timetable.uploaded"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/timetable/{year}/{semester}/{day}/{period}")
    public ResponseEntity<String> updateBySlot(
            @PathVariable String year,
            @PathVariable String semester,
            @PathVariable String day,
            @PathVariable String period,
            @RequestBody Timetable req,
            Authentication auth) {

        timetableService.updateBySlot(
                getDeptId(auth), year, semester, day, period, req
        );

        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/timetable/download")
    public ResponseEntity<byte[]> download(
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) throws IOException {

        byte[] file = timetableService.download(
                getDeptId(auth), academicYear, semester
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=timetable.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(file);
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/timetable")
    public ResponseEntity<String> deleteByFilter(
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) {

        timetableService.deleteByFilter(getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Class Incharge ─────────────────────────────────────────

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/incharge")
    public ResponseEntity<String> createIncharge(
            @RequestBody ClassInchargeRequest req,
            Authentication auth) {
        classInchargeService.create(getDeptId(auth), req);
        return ResponseEntity.ok("Created successfully");
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR', 'HOD')")
    @GetMapping("/incharge")
    public ResponseEntity<List<ClassIncharge>> getAllIncharge(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {
        return ResponseEntity.ok(classInchargeService.getAll(getDeptId(auth), academicYear));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR', 'HOD')")
    @GetMapping("/incharge/{id}")
    public ResponseEntity<ClassInchargeResponse> getById(
            @PathVariable Long id,
            Authentication auth) {

        return ResponseEntity.ok(
                classInchargeService.getById(id, getDeptId(auth))
        );
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/incharge/{id}")
    public ResponseEntity<String> updateIncharge(
            @PathVariable Long id,
            @RequestBody ClassInchargeRequest req,
            Authentication auth) {
        classInchargeService.update(id, getDeptId(auth), req);
        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/incharge/{id}")
    public ResponseEntity<String> deleteIncharge(
            @PathVariable Long id,
            Authentication auth) {
        classInchargeService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }
}

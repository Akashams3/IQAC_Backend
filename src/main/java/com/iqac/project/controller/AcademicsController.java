package com.iqac.project.controller;

import com.iqac.project.dto.ClassInchargeRequest;
import com.iqac.project.dto.ClassInchargeResponse;
import com.iqac.project.dto.ClassMentorRequest;
import com.iqac.project.dto.ClassMentorResponse;
import com.iqac.project.entity.Timetable;
import com.iqac.project.service.ClassInchargeService;
import com.iqac.project.service.ClassMentorService;
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
    private final ClassMentorService classMentorService;
    private final MessageSource messageSource;

    public AcademicsController(TimetableService timetableService, ClassInchargeService classInchargeService,
                               ClassMentorService classMentorService, MessageSource messageSource) {
        this.timetableService = timetableService;
        this.classInchargeService = classInchargeService;
        this.classMentorService = classMentorService;
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
    public ResponseEntity<List<ClassInchargeResponse>> getAllIncharge(
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
    @DeleteMapping("/incharge")
    public ResponseEntity<String> deleteInchargeByYear(
            @RequestParam String academicYear,
            Authentication auth) {
        classInchargeService.deleteByYear(getDeptId(auth), academicYear);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/incharge/{id}")
    public ResponseEntity<String> deleteIncharge(
            @PathVariable Long id,
            Authentication auth) {
        classInchargeService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Class Mentor ───────────────────────────────────────────

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/mentor")
    public ResponseEntity<String> createMentor(
            @RequestBody ClassMentorRequest req,
            Authentication auth) {
        classMentorService.create(getDeptId(auth), req);
        return ResponseEntity.ok("Created successfully");
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/mentor")
    public ResponseEntity<List<ClassMentorResponse>> getAllMentor(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {
        return ResponseEntity.ok(classMentorService.getAll(getDeptId(auth), academicYear));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/mentor/{id}")
    public ResponseEntity<ClassMentorResponse> getMentorById(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(classMentorService.getById(id, getDeptId(auth)));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/mentor/{id}")
    public ResponseEntity<String> updateMentor(
            @PathVariable Long id,
            @RequestBody ClassMentorRequest req,
            Authentication auth) {
        classMentorService.update(id, getDeptId(auth), req);
        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/mentor")
    public ResponseEntity<String> deleteMentorByYear(
            @RequestParam String academicYear,
            Authentication auth) {
        classMentorService.deleteByYear(getDeptId(auth), academicYear);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/mentor/{id}")
    public ResponseEntity<String> deleteMentor(
            @PathVariable Long id,
            Authentication auth) {
        classMentorService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }
}

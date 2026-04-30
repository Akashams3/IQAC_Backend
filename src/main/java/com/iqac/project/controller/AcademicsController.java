package com.iqac.project.controller;

import com.iqac.project.entity.ClassIncharge;
import com.iqac.project.entity.Timetable;
import com.iqac.project.service.ClassInchargeService;
import com.iqac.project.service.TimetableService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/timetable")
    public ResponseEntity<List<Timetable>> getAllTimetable(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        return ResponseEntity.ok(timetableService.getAll(getDeptId(auth), academicYear));
    }

    @PostMapping("/timetable/upload")
    public ResponseEntity<String> uploadTimetable(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            Authentication auth) throws IOException {
        timetableService.uploadExcel(file, getDeptId(auth), academicYear);
        return ResponseEntity.ok(msg("timetable.uploaded"));
    }

    @DeleteMapping("/timetable/{id}")
    public ResponseEntity<String> deleteTimetable(@PathVariable Long id, Authentication auth) {
        timetableService.delete(id, getDeptId(auth));
        return ResponseEntity.ok(msg("timetable.deleted"));
    }

    // ── Class Incharge ─────────────────────────────────────────

    @GetMapping("/incharge")
    public ResponseEntity<List<ClassIncharge>> getAllIncharge(
            @RequestParam(required = false) String academicYear, Authentication auth) {
        return ResponseEntity.ok(classInchargeService.getAll(getDeptId(auth), academicYear));
    }

    @PostMapping("/incharge/upload")
    public ResponseEntity<String> uploadIncharge(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            Authentication auth) throws IOException {
        classInchargeService.uploadExcel(file, getDeptId(auth), academicYear);
        return ResponseEntity.ok(msg("classincharge.uploaded"));
    }

    @DeleteMapping("/incharge/{id}")
    public ResponseEntity<String> deleteIncharge(@PathVariable Long id, Authentication auth) {
        classInchargeService.delete(id, getDeptId(auth));
        return ResponseEntity.ok(msg("classincharge.deleted"));
    }
}

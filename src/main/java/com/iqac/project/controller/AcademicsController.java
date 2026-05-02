package com.iqac.project.controller;

import com.iqac.project.dto.ClassInchargeRequest;
import com.iqac.project.dto.ClassInchargeResponse;
import com.iqac.project.dto.ClassMentorRequest;
import com.iqac.project.dto.ClassMentorResponse;
import com.iqac.project.dto.LessonPlanRequest;
import com.iqac.project.entity.CurriculumSyllabus;
import com.iqac.project.entity.LessonPlan;
import com.iqac.project.entity.Timetable;
import com.iqac.project.repository.FacultyRepository;
import com.iqac.project.service.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/iqac/academics")
public class AcademicsController {

    private final TimetableService timetableService;
    private final ClassInchargeService classInchargeService;
    private final ClassMentorService classMentorService;
    private final LessonPlanService lessonPlanService;
    private final CurriculumSyllabusService curriculumSyllabusService;
    private final FacultyRepository facultyRepository;
    private final MessageSource messageSource;

    public AcademicsController(TimetableService timetableService, ClassInchargeService classInchargeService,
                               ClassMentorService classMentorService, LessonPlanService lessonPlanService, CurriculumSyllabusService curriculumSyllabusService,
                               FacultyRepository facultyRepository, MessageSource messageSource) {
        this.timetableService = timetableService;
        this.classInchargeService = classInchargeService;
        this.classMentorService = classMentorService;
        this.lessonPlanService = lessonPlanService;
        this.curriculumSyllabusService = curriculumSyllabusService;
        this.facultyRepository = facultyRepository;
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
    @GetMapping("/planning/timetable")
    public ResponseEntity<List<Timetable>> getAllTimetable(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester,
            Authentication auth) {
        return ResponseEntity.ok(timetableService.getAll(getDeptId(auth), academicYear, semester));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/planning/timetable/upload")
    public ResponseEntity<String> uploadTimetable(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) throws IOException {

        timetableService.uploadExcel(file, getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok(msg("timetable.uploaded"));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/planning/timetable/{year}/{semester}/{day}/{period}")
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
    @GetMapping("/planning/timetable/download")
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
    @DeleteMapping("/planning/timetable")
    public ResponseEntity<String> deleteByFilter(
            @RequestParam String academicYear,
            @RequestParam String semester,
            Authentication auth) {

        timetableService.deleteByFilter(getDeptId(auth), academicYear, semester);
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Class Incharge ─────────────────────────────────────────

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/planning/incharge")
    public ResponseEntity<String> createIncharge(
            @RequestBody ClassInchargeRequest req,
            Authentication auth) {
        classInchargeService.create(getDeptId(auth), req);
        return ResponseEntity.ok("Created successfully");
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR', 'HOD')")
    @GetMapping("/planning/incharge")
    public ResponseEntity<List<ClassInchargeResponse>> getAllIncharge(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {
        return ResponseEntity.ok(classInchargeService.getAll(getDeptId(auth), academicYear));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR', 'HOD')")
    @GetMapping("/planning/incharge/{id}")
    public ResponseEntity<ClassInchargeResponse> getInchargeById(
            @PathVariable Long id,
            Authentication auth) {

        return ResponseEntity.ok(
                classInchargeService.getById(id, getDeptId(auth))
        );
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/planning/incharge/{id}")
    public ResponseEntity<String> updateIncharge(
            @PathVariable Long id,
            @RequestBody ClassInchargeRequest req,
            Authentication auth) {
        classInchargeService.update(id, getDeptId(auth), req);
        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/planning/incharge")
    public ResponseEntity<String> deleteInchargeByYear(
            @RequestParam String academicYear,
            Authentication auth) {
        classInchargeService.deleteByYear(getDeptId(auth), academicYear);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/planning/incharge/{id}")
    public ResponseEntity<String> deleteIncharge(
            @PathVariable Long id,
            Authentication auth) {
        classInchargeService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Class Mentor ───────────────────────────────────────────

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/planning/mentor")
    public ResponseEntity<String> createMentor(
            @RequestBody ClassMentorRequest req,
            Authentication auth) {
        classMentorService.create(getDeptId(auth), req);
        return ResponseEntity.ok("Created successfully");
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/mentor")
    public ResponseEntity<List<ClassMentorResponse>> getAllMentor(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {
        return ResponseEntity.ok(classMentorService.getAll(getDeptId(auth), academicYear));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/mentor/{id}")
    public ResponseEntity<ClassMentorResponse> getMentorById(
            @PathVariable Long id,
            Authentication auth) {

        return ResponseEntity.ok(
                classMentorService.getById(id, getDeptId(auth))
        );
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PutMapping("/planning/mentor/{id}")
    public ResponseEntity<String> updateMentor(
            @PathVariable Long id,
            @RequestBody ClassMentorRequest req,
            Authentication auth) {
        classMentorService.update(id, getDeptId(auth), req);
        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/planning/mentor")
    public ResponseEntity<String> deleteMentorByYear(
            @RequestParam String academicYear,
            Authentication auth) {
        classMentorService.deleteByYear(getDeptId(auth), academicYear);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/planning/mentor/{id}")
    public ResponseEntity<String> deleteMentor(
            @PathVariable Long id,
            Authentication auth) {
        classMentorService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Lesson Plan ────────────────────────────────────────────

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping("/planning/lesson-plan")
    public ResponseEntity<String> createLessonPlan(
            @RequestBody LessonPlanRequest req,
            Authentication auth) {
        Long facultyId = facultyRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Faculty not found")).getId();
        lessonPlanService.create(facultyId, getDeptId(auth), req);
        return ResponseEntity.ok("Created successfully");
    }

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/planning/lesson-plan/my")
    public ResponseEntity<List<LessonPlan>> getMyLessonPlans(Authentication auth) {
        Long facultyId = facultyRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Faculty not found")).getId();
        return ResponseEntity.ok(lessonPlanService.getByFaculty(facultyId));
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/lesson-plan")
    public ResponseEntity<List<LessonPlan>> getAllLessonPlans(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {
        return ResponseEntity.ok(lessonPlanService.getByDept(getDeptId(auth), academicYear));
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/planning/lesson-plan/{id}")
    public ResponseEntity<String> updateLessonPlan(
            @PathVariable Long id,
            @RequestBody LessonPlanRequest req,
            Authentication auth) {
        lessonPlanService.update(id, getDeptId(auth), req);
        return ResponseEntity.ok("Updated successfully");
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PutMapping("/planning/lesson-plan/{id}/submit")
    public ResponseEntity<String> submitLessonPlan(
            @PathVariable Long id,
            Authentication auth) {
        lessonPlanService.submit(id, getDeptId(auth));
        return ResponseEntity.ok("Submitted successfully");
    }

    @PreAuthorize("hasRole('HOD')")
    @PutMapping("/planning/lesson-plan/{id}/approve")
    public ResponseEntity<String> approveLessonPlan(
            @PathVariable Long id,
            Authentication auth) {
        lessonPlanService.approve(id, getDeptId(auth));
        return ResponseEntity.ok("Approved successfully");
    }

    @PreAuthorize("hasRole('FACULTY')")
    @DeleteMapping("/planning/lesson-plan/{id}")
    public ResponseEntity<String> deleteLessonPlan(
            @PathVariable Long id,
            Authentication auth) {
        lessonPlanService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }

    // ── Curriculum & Syllabus ─────────────────────────

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @PostMapping("/planning/curriculum-syllabus")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestParam String regulation,
            Authentication auth) throws IOException {

        return ResponseEntity.ok(
                curriculumSyllabusService.uploadFile(
                        file,
                        getDeptId(auth),
                        academicYear,
                        semester,
                        regulation
                )
        );
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/curriculum-syllabus")
    public ResponseEntity<List<CurriculumSyllabus>> getAll(
            @RequestParam(required = false) String academicYear,
            Authentication auth) {

        return ResponseEntity.ok(
                curriculumSyllabusService.getAll(getDeptId(auth), academicYear)
        );
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/curriculum-syllabus/{id}/details")
    public ResponseEntity<CurriculumSyllabus> getSyllabusById(
            @PathVariable Long id,
            Authentication auth) {

        return ResponseEntity.ok(
                curriculumSyllabusService.getById(id, getDeptId(auth))
        );
    }

    @PreAuthorize("hasAnyRole('IQAC_COORDINATOR','HOD')")
    @GetMapping("/planning/curriculum-syllabus/{id}")
    public ResponseEntity<byte[]> download(
            @PathVariable Long id,
            Authentication auth) throws IOException {

        String filePath = curriculumSyllabusService.getFilePath(id, getDeptId(auth));

        File file = curriculumSyllabusService.downloadFile(filePath);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .header("Content-Type", "application/pdf")
                .body(Files.readAllBytes(file.toPath()));
    }

    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    @DeleteMapping("/planning/curriculum-syllabus/{id}")
    public ResponseEntity<String> deleteSyllabus(
            @PathVariable Long id,
            Authentication auth) {

        curriculumSyllabusService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Deleted successfully");
    }
}

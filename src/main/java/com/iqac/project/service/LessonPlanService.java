package com.iqac.project.service;

import com.iqac.project.dto.LessonPlanRequest;
import com.iqac.project.entity.*;
import com.iqac.project.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LessonPlanService {

    private final LessonPlanRepository repo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository deptRepo;

    public LessonPlanService(LessonPlanRepository repo,
                             FacultyRepository facultyRepo,
                             DepartmentRepository deptRepo) {
        this.repo = repo;
        this.facultyRepo = facultyRepo;
        this.deptRepo = deptRepo;
    }

    // ✅ CREATE (Faculty)
    public void create(Long facultyId, Long deptId, LessonPlanRequest req) {

        Faculty faculty = facultyRepo.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        LessonPlan lp = LessonPlan.builder()
                .subject(req.getSubject())
                .unitName(req.getUnitName())
                .topic(req.getTopic())
                .plannedHours(req.getPlannedHours())
                .completedHours(req.getCompletedHours())
                .status(calcStatus(req.getPlannedHours(), req.getCompletedHours()))
                .academicYear(req.getAcademicYear())
                .semester(req.getSemester())
                .approvalStatus("DRAFT") // 🔥 default
                .faculty(faculty)
                .department(dept)
                .build();

        repo.save(lp);
    }

    // ✅ GET (Faculty)
    public List<LessonPlan> getByFaculty(Long facultyId) {
        return repo.findByFacultyId(facultyId);
    }

    public List<LessonPlan> getByDept(Long deptId, String academicYear) {
        if (academicYear != null)
            return repo.findByDepartmentIdAndAcademicYear(deptId, academicYear);
        return repo.findByDepartmentId(deptId);
    }

    // ✅ UPDATE (only before approval)
    public void update(Long id, Long deptId, LessonPlanRequest req) {

        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if ("APPROVED".equals(lp.getApprovalStatus())) {
            throw new RuntimeException("Cannot edit approved plan");
        }

        lp.setSubject(req.getSubject());
        lp.setUnitName(req.getUnitName());
        lp.setTopic(req.getTopic());
        lp.setPlannedHours(req.getPlannedHours());
        lp.setCompletedHours(req.getCompletedHours());
        lp.setAcademicYear(req.getAcademicYear());
        lp.setSemester(req.getSemester());

        lp.setStatus(calcStatus(req.getPlannedHours(), req.getCompletedHours()));

        repo.save(lp);
    }

    // ✅ SUBMIT (Faculty)
    public void submit(Long id, Long deptId) {

        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!"DRAFT".equals(lp.getApprovalStatus())) {
            throw new RuntimeException("Already submitted or approved");
        }

        lp.setApprovalStatus("SUBMITTED");
        repo.save(lp);
    }

    // ✅ APPROVE (HOD)
    public void approve(Long id, Long deptId) {

        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!"SUBMITTED".equals(lp.getApprovalStatus())) {
            throw new RuntimeException("Only submitted plans can be approved");
        }

        lp.setApprovalStatus("APPROVED");
        repo.save(lp);
    }

    // ✅ DELETE
    public void delete(Long id, Long deptId) {

        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if ("APPROVED".equals(lp.getApprovalStatus())) {
            throw new RuntimeException("Cannot delete approved plan");
        }

        repo.delete(lp);
    }

    // 🔁 STATUS CALCULATION
    private String calcStatus(int planned, int completed) {
        if (completed == 0) return "NOT_STARTED";
        if (completed < planned) return "IN_PROGRESS";
        return "COMPLETED";
    }
}
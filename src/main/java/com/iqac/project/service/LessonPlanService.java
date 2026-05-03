package com.iqac.project.service;

import com.iqac.project.dto.LessonPlanRequest;
import com.iqac.project.entity.*;
import com.iqac.project.entity.enums.ApprovalStatus;
import com.iqac.project.entity.enums.LessonPlanStatus;
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
                .approvalStatus(ApprovalStatus.DRAFT)
                .faculty(faculty)
                .department(dept)
                .build();
        repo.save(lp);
    }

    public List<LessonPlan> getByFaculty(Long facultyId) {
        return repo.findByFacultyId(facultyId);
    }

    public List<LessonPlan> getByDept(Long deptId, String academicYear) {
        if (academicYear != null)
            return repo.findByDepartmentIdAndAcademicYear(deptId, academicYear);
        return repo.findByDepartmentId(deptId);
    }

    public void update(Long id, Long deptId, LessonPlanRequest req) {
        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (lp.getApprovalStatus() == ApprovalStatus.APPROVED)
            throw new RuntimeException("Cannot edit approved plan");
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

    public void submit(Long id, Long deptId) {
        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (lp.getApprovalStatus() != ApprovalStatus.DRAFT)
            throw new RuntimeException("Already submitted or approved");
        lp.setApprovalStatus(ApprovalStatus.SUBMITTED);
        repo.save(lp);
    }

    public void approve(Long id, Long deptId) {
        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (lp.getApprovalStatus() != ApprovalStatus.SUBMITTED)
            throw new RuntimeException("Only submitted plans can be approved");
        lp.setApprovalStatus(ApprovalStatus.APPROVED);
        repo.save(lp);
    }

    public void delete(Long id, Long deptId) {
        LessonPlan lp = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (lp.getApprovalStatus() == ApprovalStatus.APPROVED)
            throw new RuntimeException("Cannot delete approved plan");
        repo.delete(lp);
    }

    private LessonPlanStatus calcStatus(int planned, int completed) {
        if (completed == 0) return LessonPlanStatus.NOT_STARTED;
        if (completed < planned) return LessonPlanStatus.IN_PROGRESS;
        return LessonPlanStatus.COMPLETED;
    }
}
package com.iqac.project.service;

import com.iqac.project.dto.ClassMentorRequest;
import com.iqac.project.dto.ClassMentorResponse;
import com.iqac.project.entity.ClassMentor;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Faculty;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.ClassMentorRepository;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.FacultyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ClassMentorService {

    private final ClassMentorRepository repo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository deptRepo;

    public ClassMentorService(ClassMentorRepository repo,
                              FacultyRepository facultyRepo,
                              DepartmentRepository deptRepo) {
        this.repo = repo;
        this.facultyRepo = facultyRepo;
        this.deptRepo = deptRepo;
    }

    public void create(Long deptId, ClassMentorRequest req) {
        log.info("Creating class mentor for class={}, year={}, dept={}", req.getClassName(), req.getAcademicYear(), deptId);
        if (repo.existsByFacultyIdAndAcademicYear(req.getFacultyId(), req.getAcademicYear()))
            throw new RuntimeException("Faculty already assigned as mentor for this year");

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        repo.save(ClassMentor.builder()
                .className(req.getClassName())
                .academicYear(req.getAcademicYear())
                .faculty(faculty)
                .department(dept)
                .build());
        log.info("Class mentor created for class={}", req.getClassName());
    }

    public List<ClassMentorResponse> getAll(Long deptId, String academicYear) {
        log.info("Fetching class mentors for dept={}, year={}", deptId, academicYear);
        List<ClassMentor> list = (academicYear != null && !academicYear.isBlank())
                ? repo.findByDepartmentIdAndAcademicYear(deptId, academicYear)
                : repo.findByDepartmentId(deptId);
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ClassMentorResponse getById(Long id, Long deptId) {
        log.info("Fetching class mentor id={} for dept={}", id, deptId);
        return mapToResponse(repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    public void update(Long id, Long deptId, ClassMentorRequest req) {
        log.info("Updating class mentor id={}", id);
        ClassMentor existing = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        existing.setClassName(req.getClassName());
        existing.setAcademicYear(req.getAcademicYear());
        existing.setFaculty(faculty);
        repo.save(existing);
        log.info("Class mentor id={} updated", id);
    }

    public void deleteByYear(Long deptId, String academicYear) {
        log.info("Deleting class mentors for dept={}, year={}", deptId, academicYear);
        if (!repo.existsByDepartmentIdAndAcademicYear(deptId, academicYear))
            throw new ResourceNotFoundException("No records found for this academic year");
        repo.deleteByDepartmentIdAndAcademicYear(deptId, academicYear);
    }

    public void delete(Long id, Long deptId) {
        log.info("Deleting class mentor id={}", id);
        repo.delete(repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found")));
    }

    private ClassMentorResponse mapToResponse(ClassMentor e) {
        return ClassMentorResponse.builder()
                .id(e.getId())
                .className(e.getClassName())
                .academicYear(e.getAcademicYear())
                .facultyId(e.getFaculty().getId())
                .facultyName(e.getFaculty().getFacultyName())
                .email(e.getFaculty().getEmail())
                .build();
    }
}

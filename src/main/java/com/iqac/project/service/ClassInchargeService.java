package com.iqac.project.service;

import com.iqac.project.dto.ClassInchargeRequest;
import com.iqac.project.dto.ClassInchargeResponse;
import com.iqac.project.entity.ClassIncharge;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Faculty;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.ClassInchargeRepository;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.FacultyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ClassInchargeService {

    private final ClassInchargeRepository repo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository deptRepo;

    public ClassInchargeService(ClassInchargeRepository repo,
                                FacultyRepository facultyRepo,
                                DepartmentRepository deptRepo) {
        this.repo = repo;
        this.facultyRepo = facultyRepo;
        this.deptRepo = deptRepo;
    }

    public void create(Long deptId, ClassInchargeRequest req) {
        log.info("Creating class incharge for class={}, year={}, dept={}", req.getClassName(), req.getAcademicYear(), deptId);
        if (repo.existsByClassNameAndAcademicYear(req.getClassName(), req.getAcademicYear()))
            throw new RuntimeException("Class already has an incharge");
        if (repo.existsByFacultyIdAndAcademicYear(req.getFacultyId(), req.getAcademicYear()))
            throw new RuntimeException("Faculty already assigned to another class");

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        repo.save(ClassIncharge.builder()
                .className(req.getClassName())
                .academicYear(req.getAcademicYear())
                .faculty(faculty)
                .department(dept)
                .build());
        log.info("Class incharge created for class={}", req.getClassName());
    }

    public List<ClassInchargeResponse> getAll(Long deptId, String academicYear) {
        log.info("Fetching class incharge list for dept={}, year={}", deptId, academicYear);
        List<ClassIncharge> list = academicYear != null
                ? repo.findByDepartmentIdAndAcademicYear(deptId, academicYear)
                : repo.findByDepartmentId(deptId);
        return list.stream().map(e -> ClassInchargeResponse.builder()
                .id(e.getId())
                .className(e.getClassName())
                .academicYear(e.getAcademicYear())
                .facultyId(e.getFaculty().getId())
                .facultyName(e.getFaculty().getFacultyName())
                .email(e.getFaculty().getEmail())
                .build()).toList();
    }

    public ClassInchargeResponse getById(Long id, Long deptId) {
        log.info("Fetching class incharge id={} for dept={}", id, deptId);
        ClassIncharge entity = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return ClassInchargeResponse.builder()
                .id(entity.getId())
                .className(entity.getClassName())
                .academicYear(entity.getAcademicYear())
                .facultyId(entity.getFaculty().getId())
                .facultyName(entity.getFaculty().getFacultyName())
                .email(entity.getFaculty().getEmail())
                .build();
    }

    public void update(Long id, Long deptId, ClassInchargeRequest req) {
        log.info("Updating class incharge id={}", id);
        ClassIncharge existing = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        if (!existing.getClassName().equals(req.getClassName()) &&
                repo.existsByClassNameAndAcademicYear(req.getClassName(), req.getAcademicYear()))
            throw new RuntimeException("Class already assigned");
        if (!existing.getFaculty().getId().equals(req.getFacultyId()) &&
                repo.existsByFacultyIdAndAcademicYear(req.getFacultyId(), req.getAcademicYear()))
            throw new RuntimeException("Faculty already assigned");

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        existing.setClassName(req.getClassName());
        existing.setAcademicYear(req.getAcademicYear());
        existing.setFaculty(faculty);
        repo.save(existing);
        log.info("Class incharge id={} updated", id);
    }

    public void deleteByYear(Long deptId, String academicYear) {
        log.info("Deleting class incharge for dept={}, year={}", deptId, academicYear);
        repo.deleteByDepartmentIdAndAcademicYear(deptId, academicYear);
    }

    public void delete(Long id, Long deptId) {
        log.info("Deleting class incharge id={}", id);
        ClassIncharge entity = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        repo.delete(entity);
    }
}

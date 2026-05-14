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
import com.iqac.project.util.AcademicYearUtil;
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
        String ay = AcademicYearUtil.normalizeToShort(AcademicYearUtil.blankToNull(req.getAcademicYear()));
        String[] yp = AcademicYearUtil.filterPair(ay);
        if (repo.existsByClassNameAndAcademicYearEither(req.getClassName(), yp[0], yp[1]))
            throw new RuntimeException("Class already has an incharge");
        if (repo.existsByFacultyIdAndAcademicYearEither(req.getFacultyId(), yp[0], yp[1]))
            throw new RuntimeException("Faculty already assigned to another class");

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        repo.save(ClassIncharge.builder()
                .className(req.getClassName())
                .academicYear(ay)
                .faculty(faculty)
                .department(dept)
                .build());
        log.info("Class incharge created for class={}", req.getClassName());
    }

    public List<ClassInchargeResponse> getAll(Long deptId, String academicYear) {
        log.info("Fetching class incharge list for dept={}, year={}", deptId, academicYear);
        String[] yp = AcademicYearUtil.filterPair(academicYear);
        List<ClassIncharge> list = yp[0] != null
                ? repo.findByDepartmentIdAndAcademicYearFlexible(deptId, yp[0], yp[1])
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
        String[] yp = AcademicYearUtil.filterPair(
                AcademicYearUtil.normalizeToShort(AcademicYearUtil.blankToNull(req.getAcademicYear())));
        if (!existing.getClassName().equals(req.getClassName()) &&
                repo.existsByClassNameAndAcademicYearEither(req.getClassName(), yp[0], yp[1]))
            throw new RuntimeException("Class already assigned");
        if (!existing.getFaculty().getId().equals(req.getFacultyId()) &&
                repo.existsByFacultyIdAndAcademicYearEither(req.getFacultyId(), yp[0], yp[1]))
            throw new RuntimeException("Faculty already assigned");

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        existing.setClassName(req.getClassName());
        existing.setAcademicYear(AcademicYearUtil.normalizeToShort(AcademicYearUtil.blankToNull(req.getAcademicYear())));
        existing.setFaculty(faculty);
        repo.save(existing);
        log.info("Class incharge id={} updated", id);
    }

    public void deleteByYear(Long deptId, String academicYear) {
        log.info("Deleting class incharge for dept={}, year={}", deptId, academicYear);
        String[] yp = AcademicYearUtil.filterPair(academicYear);
        repo.deleteByDepartmentIdAndAcademicYearEither(deptId, yp[0], yp[1]);
    }

    public void delete(Long id, Long deptId) {
        log.info("Deleting class incharge id={}", id);
        ClassIncharge entity = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        repo.delete(entity);
    }
}

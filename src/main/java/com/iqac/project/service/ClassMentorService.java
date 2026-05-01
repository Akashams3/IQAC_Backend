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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // ✅ CREATE
    public void create(Long deptId, ClassMentorRequest req) {

        if (repo.existsByFacultyIdAndAcademicYear(req.getFacultyId(), req.getAcademicYear())) {
            throw new RuntimeException("Faculty already assigned as mentor for this year");
        }

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        ClassMentor entity = ClassMentor.builder()
                .className(req.getClassName())
                .academicYear(req.getAcademicYear())
                .faculty(faculty)
                .department(dept)
                .build();

        repo.save(entity);
    }

    // ✅ GET ALL (with filter)
    public List<ClassMentorResponse> getAll(Long deptId, String academicYear) {

        List<ClassMentor> list;

        if (academicYear != null && !academicYear.isBlank()) {
            list = repo.findByDepartmentIdAndAcademicYear(deptId, academicYear);
        } else {
            list = repo.findByDepartmentId(deptId);
        }

        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ✅ GET BY ID
    public ClassMentorResponse getById(Long id, Long deptId) {

        ClassMentor entity = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        return mapToResponse(entity);
    }

    // ✅ UPDATE
    public void update(Long id, Long deptId, ClassMentorRequest req) {

        ClassMentor existing = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        Faculty faculty = facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        existing.setClassName(req.getClassName());
        existing.setAcademicYear(req.getAcademicYear());
        existing.setFaculty(faculty);

        repo.save(existing);
    }

    // ✅ DELETE BY YEAR
    public void deleteByYear(Long deptId, String academicYear) {
        if (!repo.existsByDepartmentIdAndAcademicYear(deptId, academicYear))
            throw new ResourceNotFoundException("No records found for this academic year");
        repo.deleteByDepartmentIdAndAcademicYear(deptId, academicYear);
    }

    // ✅ DELETE
    public void delete(Long id, Long deptId) {

        ClassMentor entity = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        repo.delete(entity);
    }

    // 🔁 Mapper
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
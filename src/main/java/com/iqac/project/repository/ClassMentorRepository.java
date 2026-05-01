package com.iqac.project.repository;

import com.iqac.project.entity.ClassMentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassMentorRepository extends JpaRepository<ClassMentor, Long> {

    List<ClassMentor> findByDepartmentId(Long deptId);

    List<ClassMentor> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    Optional<ClassMentor> findByIdAndDepartmentId(Long id, Long deptId);

    boolean existsByFacultyIdAndAcademicYear(Long facultyId, String academicYear);
    void deleteByDepartmentIdAndAcademicYear(Long deptId, String academicYear);
    boolean existsByDepartmentIdAndAcademicYear(Long deptId, String academicYear);
}
package com.iqac.project.repository;

import com.iqac.project.entity.ClassIncharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassInchargeRepository extends JpaRepository<ClassIncharge, Long> {

    List<ClassIncharge> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    Optional<ClassIncharge> findByIdAndDepartmentId(Long id, Long deptId);

    boolean existsByClassNameAndAcademicYear(String className, String academicYear);

    boolean existsByFacultyIdAndAcademicYear(Long facultyId, String academicYear);
    List<ClassIncharge> findByDepartmentId(Long deptId);
}
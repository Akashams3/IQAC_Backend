package com.iqac.project.repository;

import com.iqac.project.entity.CurriculumSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumSyllabusRepository extends JpaRepository<CurriculumSyllabus, Long> {

    List<CurriculumSyllabus> findByDepartmentId(Long deptId);

    List<CurriculumSyllabus> findByDepartmentIdAndAcademicYear(Long deptId, String year);
}
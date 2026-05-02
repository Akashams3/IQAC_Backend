package com.iqac.project.repository;

import com.iqac.project.entity.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {

    List<LessonPlan> findByFacultyId(Long facultyId);

    List<LessonPlan> findByDepartmentId(Long deptId);

    List<LessonPlan> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    Optional<LessonPlan> findByIdAndDepartmentId(Long id, Long deptId);
}
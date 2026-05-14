package com.iqac.project.repository;

import com.iqac.project.entity.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {

    List<LessonPlan> findByFacultyId(Long facultyId);

    List<LessonPlan> findByDepartmentId(Long deptId);

    List<LessonPlan> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    @Query("SELECT lp FROM LessonPlan lp WHERE lp.department.id = :deptId " +
           "AND (:ay IS NULL OR lp.academicYear = :ay OR lp.academicYear = :ayAlt)")
    List<LessonPlan> findByDepartmentIdAndAcademicYearFlexible(
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    Optional<LessonPlan> findByIdAndDepartmentId(Long id, Long deptId);
}
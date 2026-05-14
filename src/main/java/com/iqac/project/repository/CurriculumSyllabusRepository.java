package com.iqac.project.repository;

import com.iqac.project.entity.CurriculumSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurriculumSyllabusRepository extends JpaRepository<CurriculumSyllabus, Long> {

    List<CurriculumSyllabus> findByDepartmentId(Long deptId);

    List<CurriculumSyllabus> findByDepartmentIdAndAcademicYear(Long deptId, String year);

    @Query("SELECT c FROM CurriculumSyllabus c WHERE c.department.id = :deptId " +
           "AND (:ay IS NULL OR c.academicYear = :ay OR c.academicYear = :ayAlt)")
    List<CurriculumSyllabus> findByDepartmentIdAndAcademicYearFlexible(
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);
}
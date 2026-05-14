package com.iqac.project.repository;

import com.iqac.project.entity.CocmMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CocmMemberRepository extends JpaRepository<CocmMember, Long> {

    List<CocmMember> findByDepartmentId(Long deptId);
    List<CocmMember> findByDepartmentIdAndAcademicYear(Long deptId, String year);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM CocmMember m WHERE m.name = :name "
           + "AND m.department.id = :deptId AND (m.academicYear = :ay OR m.academicYear = :ayAlt)")
    boolean existsByNameAndDepartmentIdAndAcademicYearEither(
            @Param("name") String name,
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Query("SELECT m FROM CocmMember m WHERE m.department.id = :deptId "
           + "AND (:academicYear IS NULL OR m.academicYear = :academicYear OR m.academicYear = :academicYearAlt) "
           + "AND (:memberRole IS NULL OR m.role = :memberRole)")
    List<CocmMember> findByFilters(
            @Param("deptId") Long deptId,
            @Param("academicYear") String academicYear,
            @Param("academicYearAlt") String academicYearAlt,
            @Param("memberRole") String role);
}
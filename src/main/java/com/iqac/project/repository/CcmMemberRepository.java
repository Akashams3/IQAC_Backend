package com.iqac.project.repository;

import com.iqac.project.entity.CcmMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CcmMemberRepository extends JpaRepository<CcmMember, Long> {

    boolean existsByNameAndClassNameAndAcademicYearAndDepartmentId(
            String name, String className, String year, Long deptId);

    Optional<CcmMember> findByIdAndDepartmentId(Long id, Long deptId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM CcmMember m WHERE m.name = :name "
           + "AND m.className = :className AND m.department.id = :deptId "
           + "AND (m.academicYear = :ay OR m.academicYear = :ayAlt)")
    boolean existsByNameClassDeptYearEither(
            @Param("name") String name,
            @Param("className") String className,
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Query("SELECT m FROM CcmMember m WHERE m.department.id = :deptId " +
           "AND (:academicYear IS NULL OR m.academicYear = :academicYear OR m.academicYear = :academicYearAlt) " +
           "AND (:className IS NULL OR m.className = :className) " +
           "AND (:memberRole IS NULL OR m.role = :memberRole)")
    List<CcmMember> findByFilters(
            @Param("deptId") Long deptId,
            @Param("academicYear") String academicYear,
            @Param("academicYearAlt") String academicYearAlt,
            @Param("className") String className,
            @Param("memberRole") String role);
}
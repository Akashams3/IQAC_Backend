package com.iqac.project.repository;

import com.iqac.project.entity.CocmMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CocmMemberRepository extends JpaRepository<CocmMember, Long> {

    List<CocmMember> findByDepartmentId(Long deptId);
    List<CocmMember> findByDepartmentIdAndAcademicYear(Long deptId, String year);
    boolean existsByNameAndDepartmentIdAndAcademicYear(String name, Long deptId, String academicYear);

    @org.springframework.data.jpa.repository.Query(
        "SELECT m FROM CocmMember m WHERE m.department.id = :deptId " +
        "AND (:academicYear IS NULL OR m.academicYear = :academicYear) " +
        "AND (:memberRole IS NULL OR m.role = :memberRole)")
    List<CocmMember> findByFilters(
        @org.springframework.data.repository.query.Param("deptId") Long deptId,
        @org.springframework.data.repository.query.Param("academicYear") String academicYear,
        @org.springframework.data.repository.query.Param("memberRole") String role);
}
package com.iqac.project.repository;

import com.iqac.project.entity.ClassIncharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClassInchargeRepository extends JpaRepository<ClassIncharge, Long> {

    List<ClassIncharge> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    Optional<ClassIncharge> findByIdAndDepartmentId(Long id, Long deptId);

    boolean existsByClassNameAndAcademicYear(String className, String academicYear);

    boolean existsByFacultyIdAndAcademicYear(Long facultyId, String academicYear);

    List<ClassIncharge> findByDepartmentId(Long deptId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ClassIncharge c WHERE c.department.id = :deptId AND c.academicYear = :academicYear")
    boolean existsByDepartmentIdAndAcademicYear(@Param("deptId") Long deptId, @Param("academicYear") String academicYear);

    @Modifying
    @Transactional
    @Query("DELETE FROM ClassIncharge c WHERE c.department.id = :deptId AND c.academicYear = :academicYear")
    void deleteByDepartmentIdAndAcademicYear(@Param("deptId") Long deptId, @Param("academicYear") String academicYear);
}
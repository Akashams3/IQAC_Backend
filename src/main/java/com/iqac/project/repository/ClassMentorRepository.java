package com.iqac.project.repository;

import com.iqac.project.entity.ClassMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClassMentorRepository extends JpaRepository<ClassMentor, Long> {

    List<ClassMentor> findByDepartmentId(Long deptId);

    List<ClassMentor> findByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    Optional<ClassMentor> findByIdAndDepartmentId(Long id, Long deptId);

    boolean existsByFacultyIdAndAcademicYear(Long facultyId, String academicYear);
    void deleteByDepartmentIdAndAcademicYear(Long deptId, String academicYear);
    boolean existsByDepartmentIdAndAcademicYear(Long deptId, String academicYear);

    @Query("SELECT m FROM ClassMentor m WHERE m.department.id = :deptId "
           + "AND (:ay IS NULL OR m.academicYear = :ay OR m.academicYear = :ayAlt)")
    List<ClassMentor> findByDepartmentIdAndAcademicYearFlexible(
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Query("SELECT COUNT(m) FROM ClassMentor m WHERE m.faculty.id = :fid "
           + "AND (m.academicYear = :ay OR m.academicYear = :ayAlt)")
    long countByFacultyIdAndAcademicYearEither(
            @Param("fid") Long facultyId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Query("SELECT COUNT(m) FROM ClassMentor m WHERE m.department.id = :deptId "
           + "AND (m.academicYear = :ay OR m.academicYear = :ayAlt)")
    long countByDepartmentIdAndAcademicYearEither(
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Modifying
    @Transactional
    @Query("DELETE FROM ClassMentor m WHERE m.department.id = :deptId "
           + "AND (m.academicYear = :ay OR m.academicYear = :ayAlt)")
    void deleteByDepartmentIdAndAcademicYearEither(
            @Param("deptId") Long deptId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);
}
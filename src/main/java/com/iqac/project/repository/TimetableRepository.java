package com.iqac.project.repository;

import com.iqac.project.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDepartmentId(Long departmentId);
    List<Timetable> findByDepartmentIdAndAcademicYear(Long departmentId, String academicYear);
    List<Timetable> findByDepartmentIdAndSemester(Long departmentId, String semester);
    List<Timetable> findByDepartmentIdAndAcademicYearAndSemester(
            Long departmentId, String academicYear, String semester);
    boolean existsByDepartmentIdAndAcademicYearAndSemester(
            Long departmentId, String academicYear, String semester);
    Optional<Timetable> findByDepartmentIdAndAcademicYearAndSemesterAndDayAndPeriod(
            Long deptId, String year, String sem, String day, String period);

    @Modifying
    @Transactional
    @Query("DELETE FROM Timetable t WHERE t.department.id = :deptId AND t.academicYear = :academicYear AND t.semester = :semester")
    void deleteByDepartmentIdAndAcademicYearAndSemester(
            @Param("deptId") Long departmentId,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester);
}

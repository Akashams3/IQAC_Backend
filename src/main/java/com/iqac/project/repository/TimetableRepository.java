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

    @Query("SELECT t FROM Timetable t WHERE t.department.id = :deptId AND t.semester = :sem AND t.day = :day AND t.period = :period "
           + "AND (t.academicYear = :ay OR t.academicYear = :ayAlt)")
    Optional<Timetable> findSlotByDeptSemDayPeriodYearEither(
            @Param("deptId") Long deptId,
            @Param("sem") String sem,
            @Param("day") String day,
            @Param("period") String period,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt);

    @Modifying
    @Transactional
    @Query("DELETE FROM Timetable t WHERE t.department.id = :deptId AND t.semester = :semester " +
           "AND (t.academicYear = :academicYear OR t.academicYear = :academicYearAlt)")
    void deleteByDepartmentIdAndAcademicYearAndSemester(
            @Param("deptId") Long departmentId,
            @Param("academicYear") String academicYear,
            @Param("academicYearAlt") String academicYearAlt,
            @Param("semester") String semester);

    @Query("SELECT t FROM Timetable t WHERE t.department.id = :deptId " +
           "AND (:sem IS NULL OR t.semester = :sem) " +
           "AND (:ay IS NULL OR t.academicYear = :ay OR t.academicYear = :ayAlt)")
    List<Timetable> findByDepartmentFlexible(
            @Param("deptId") Long departmentId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt,
            @Param("sem") String semester);

    @Query("SELECT COUNT(t) FROM Timetable t WHERE t.department.id = :deptId AND t.semester = :sem " +
           "AND (t.academicYear = :ay OR t.academicYear = :ayAlt)")
    long countByDepartmentSemesterAndAcademicYearEither(
            @Param("deptId") Long departmentId,
            @Param("ay") String ay,
            @Param("ayAlt") String ayAlt,
            @Param("sem") String semester);
}

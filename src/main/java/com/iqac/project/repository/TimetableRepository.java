package com.iqac.project.repository;

import com.iqac.project.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDepartmentId(Long departmentId);
    List<Timetable> findByDepartmentIdAndAcademicYear(Long departmentId, String academicYear);
}

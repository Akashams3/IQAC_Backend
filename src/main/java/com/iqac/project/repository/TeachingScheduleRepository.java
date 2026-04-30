package com.iqac.project.repository;

import com.iqac.project.entity.TeachingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeachingScheduleRepository extends JpaRepository<TeachingSchedule, Long> {
    List<TeachingSchedule> findByDepartmentId(Long departmentId);
    List<TeachingSchedule> findByDepartmentIdAndAcademicYear(Long departmentId, String academicYear);
}

package com.iqac.project.repository;

import com.iqac.project.entity.ClassIncharge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassInchargeRepository extends JpaRepository<ClassIncharge, Long> {
    List<ClassIncharge> findByDepartmentId(Long departmentId);
    List<ClassIncharge> findByDepartmentIdAndAcademicYear(Long departmentId, String academicYear);
}

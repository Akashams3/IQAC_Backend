package com.iqac.project.repository;

import com.iqac.project.entity.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanningRepository extends JpaRepository<Planning, Long> {
    List<Planning> findByDepartmentId(Long departmentId);
}

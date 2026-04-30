package com.iqac.project.repository;

import com.iqac.project.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByDepartmentId(Long departmentId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}

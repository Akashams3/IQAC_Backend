package com.iqac.project.repository;

import com.iqac.project.entity.Hod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HodRepository extends JpaRepository<Hod, Long> {
    List<Hod> findByDepartmentId(Long departmentId);
    Optional<Hod> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}

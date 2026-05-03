package com.iqac.project.repository;

import com.iqac.project.entity.Material;
import com.iqac.project.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByFacultyId(Long facultyId);

    @Query("SELECT m FROM Material m WHERE m.department.id = :deptId " +
           "AND (:academicYear IS NULL OR m.academicYear = :academicYear) " +
           "AND (:semester IS NULL OR m.semester = :semester) " +
           "AND (:status IS NULL OR m.status = :status)")
    List<Material> findByFilters(
            @Param("deptId") Long deptId,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester,
            @Param("status") Status status);
}
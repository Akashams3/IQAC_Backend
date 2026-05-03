package com.iqac.project.repository;

import com.iqac.project.entity.EResource;
import com.iqac.project.entity.enums.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EResourceRepository extends JpaRepository<EResource, Long> {

    Optional<EResource> findByIdAndDepartmentId(Long id, Long deptId);

    @Query("SELECT r FROM EResource r WHERE r.department.id = :deptId " +
           "AND (:academicYear IS NULL OR r.academicYear = :academicYear) " +
           "AND (:className IS NULL OR r.className = :className) " +
           "AND (:subject IS NULL OR r.subject = :subject) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:status IS NULL OR r.status = :status)")
    List<EResource> findByFilters(
            @Param("deptId") Long deptId,
            @Param("academicYear") String academicYear,
            @Param("className") String className,
            @Param("subject") String subject,
            @Param("type") String type,
            @Param("status") ResourceStatus status);

    List<EResource> findByFacultyId(Long facultyId);
}
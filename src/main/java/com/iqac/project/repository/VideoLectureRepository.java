package com.iqac.project.repository;

import com.iqac.project.entity.VideoLecture;
import com.iqac.project.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoLectureRepository extends JpaRepository<VideoLecture, Long> {

    List<VideoLecture> findByFacultyId(Long facultyId);

    Optional<VideoLecture> findByIdAndDepartmentId(Long id, Long deptId);

    @Query("SELECT v FROM VideoLecture v WHERE v.department.id = :deptId " +
           "AND (:subject IS NULL OR v.subject = :subject) " +
           "AND (:academicYear IS NULL OR v.academicYear = :academicYear) " +
           "AND (:className IS NULL OR v.className = :className) " +
           "AND (:status IS NULL OR v.status = :status)")
    List<VideoLecture> findByFilters(
            @Param("deptId") Long deptId,
            @Param("subject") String subject,
            @Param("academicYear") String academicYear,
            @Param("className") String className,
            @Param("status") Status status);
}

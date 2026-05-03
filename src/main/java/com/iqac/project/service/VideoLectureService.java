package com.iqac.project.service;

import com.iqac.project.dto.VideoLectureRequest;
import com.iqac.project.entity.*;
import com.iqac.project.entity.enums.Status;
import com.iqac.project.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class VideoLectureService {

    private final VideoLectureRepository repo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository deptRepo;

    public VideoLectureService(VideoLectureRepository repo,
                               FacultyRepository facultyRepo,
                               DepartmentRepository deptRepo) {
        this.repo = repo;
        this.facultyRepo = facultyRepo;
        this.deptRepo = deptRepo;
    }

    public void create(VideoLectureRequest req, Long facultyId, Long deptId) {
        log.info("Creating video lecture for faculty={}, dept={}", facultyId, deptId);
        if (req.getVideoUrl() == null || req.getVideoUrl().isBlank())
            throw new RuntimeException("Video URL required");

        Faculty faculty = facultyRepo.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        repo.save(VideoLecture.builder()
                .title(req.getTitle())
                .subject(req.getSubject())
                .academicYear(req.getAcademicYear())
                .className(req.getClassName())
                .videoUrl(req.getVideoUrl())
                .status(Status.DRAFT)
                .faculty(faculty)
                .department(dept)
                .build());
        log.info("Video lecture created for faculty={}", facultyId);
    }

    public List<VideoLecture> getMy(Long facultyId) {
        log.info("Fetching video lectures for faculty={}", facultyId);
        return repo.findByFacultyId(facultyId);
    }

    public VideoLecture getById(Long id, Long deptId) {
        log.info("Fetching video lecture id={} for dept={}", id, deptId);
        return repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public List<VideoLecture> getAll(Long deptId, String subject, String academicYear,
                                     String className, String status) {
        log.info("Fetching video lectures for dept={}, subject={}, year={}, class={}, status={}", deptId, subject, academicYear, className, status);
        Status statusEnum = (status != null) ? Status.valueOf(status.toUpperCase(Locale.ROOT)) : null;
        return repo.findByFilters(deptId, subject, academicYear, className, statusEnum);
    }

    public void update(Long id, Long facultyId, Long deptId, VideoLectureRequest req) {
        log.info("Updating video lecture id={} by faculty={}", id, facultyId);
        VideoLecture v = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (v.getStatus() != Status.DRAFT)
            throw new RuntimeException("Only DRAFT can be updated");
        if (!v.getFaculty().getId().equals(facultyId))
            throw new RuntimeException("You can only update your own lectures");
        v.setTitle(req.getTitle());
        v.setSubject(req.getSubject());
        v.setAcademicYear(req.getAcademicYear());
        v.setClassName(req.getClassName());
        v.setVideoUrl(req.getVideoUrl());
        repo.save(v);
        log.info("Video lecture id={} updated", id);
    }

    public void submit(Long id, Long deptId) {
        log.info("Submitting video lecture id={}", id);
        VideoLecture v = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (v.getStatus() != Status.DRAFT)
            throw new RuntimeException("Only DRAFT can be submitted");
        v.setStatus(Status.SUBMITTED);
        repo.save(v);
    }

    public void approve(Long id, Long deptId) {
        log.info("Approving video lecture id={}", id);
        VideoLecture v = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (v.getStatus() != Status.SUBMITTED)
            throw new RuntimeException("Only SUBMITTED can be approved");
        v.setStatus(Status.APPROVED);
        repo.save(v);
    }

    public void reject(Long id, Long deptId) {
        log.info("Rejecting video lecture id={}", id);
        VideoLecture v = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (v.getStatus() != Status.SUBMITTED)
            throw new RuntimeException("Only SUBMITTED can be rejected");
        v.setStatus(Status.REJECTED);
        repo.save(v);
    }

    public void delete(Long id, Long facultyId, Long deptId) {
        log.info("Deleting video lecture id={} by faculty={}", id, facultyId);
        VideoLecture v = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (!v.getFaculty().getId().equals(facultyId))
            throw new RuntimeException("You can only delete your own lectures");
        if (v.getStatus() != Status.DRAFT)
            throw new RuntimeException("Cannot delete after submission");
        repo.delete(v);
        log.info("Video lecture id={} deleted", id);
    }
}

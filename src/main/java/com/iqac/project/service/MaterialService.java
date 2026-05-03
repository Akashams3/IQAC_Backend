package com.iqac.project.service;

import com.iqac.project.dto.MaterialRequest;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Faculty;
import com.iqac.project.entity.Material;
import com.iqac.project.entity.enums.Status;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.FacultyRepository;
import com.iqac.project.repository.MaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MaterialService {

    private final MaterialRepository repo;
    private final FacultyRepository facultyRepo;
    private final DepartmentRepository deptRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public MaterialService(MaterialRepository repo,
                           FacultyRepository facultyRepo,
                           DepartmentRepository deptRepo) {
        this.repo = repo;
        this.facultyRepo = facultyRepo;
        this.deptRepo = deptRepo;
    }

    // ✅ CREATE (UPLOAD)
    public void create(MultipartFile file, Long facultyId, Long deptId, MaterialRequest req) throws IOException {

        if (!file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF allowed");
        }

        File dir = new File(uploadDir + "/materials/");
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(dir, fileName);
        file.transferTo(dest);

        Faculty faculty = facultyRepo.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Material material = Material.builder()
                .title(req.getTitle())
                .subject(req.getSubject())
                .academicYear(req.getAcademicYear())
                .semester(req.getSemester())
                .fileName(file.getOriginalFilename())
                .filePath(dest.getAbsolutePath())
                .status(Status.DRAFT)
                .faculty(faculty)
                .department(dept)
                .build();

        repo.save(material);
    }

    // ✅ MY MATERIALS
    public List<Material> getMy(Long facultyId) {
        return repo.findByFacultyId(facultyId);
    }

    public List<Material> getAll(Long deptId, String academicYear, String semester, String status) {
        Status statusEnum = (status != null) ? Status.valueOf(status.toUpperCase()) : null;
        return repo.findByFilters(deptId, academicYear, semester, statusEnum);
    }

    // ✅ SUBMIT
    public void update(Long id, Long facultyId, Long deptId, MaterialRequest req) {
        Material m = get(id, deptId);
        if (m.getStatus() != Status.DRAFT)
            throw new RuntimeException("Only DRAFT materials can be updated");
        if (!m.getFaculty().getId().equals(facultyId))
            throw new RuntimeException("You can only update your own materials");
        m.setTitle(req.getTitle());
        m.setSubject(req.getSubject());
        m.setAcademicYear(req.getAcademicYear());
        m.setSemester(req.getSemester());
        repo.save(m);
    }

    public void submit(Long id, Long deptId) {
        Material m = get(id, deptId);

        if (m.getStatus() != Status.DRAFT) {
            throw new RuntimeException("Only draft can be submitted");
        }

        m.setStatus(Status.SUBMITTED);
        repo.save(m);
    }

    // ✅ APPROVE
    public void approve(Long id, Long deptId) {
        Material m = get(id, deptId);
        if (m.getStatus() != Status.SUBMITTED)
            throw new RuntimeException("Only submitted can be approved");
        m.setStatus(Status.APPROVED);
        repo.save(m);
    }

    public void reject(Long id, Long deptId) {
        Material m = get(id, deptId);
        if (m.getStatus() != Status.SUBMITTED)
            throw new RuntimeException("Only submitted can be rejected");
        m.setStatus(Status.REJECTED);
        repo.save(m);
    }

    // ✅ DOWNLOAD
    public File download(Long id, Long deptId) {
        Material m = get(id, deptId);
        return new File(m.getFilePath());
    }

    // ✅ DELETE
    public void delete(Long id, Long deptId, String role) {
        Material m = get(id, deptId);
        if (!role.equals("IQAC_COORDINATOR") && m.getStatus() != Status.DRAFT)
            throw new RuntimeException("Cannot delete after submission");
        new File(m.getFilePath()).delete();
        repo.delete(m);
    }

    private Material get(Long id, Long deptId) {
        Material m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!m.getDepartment().getId().equals(deptId)) {
            throw new RuntimeException("Unauthorized");
        }

        return m;
    }
}
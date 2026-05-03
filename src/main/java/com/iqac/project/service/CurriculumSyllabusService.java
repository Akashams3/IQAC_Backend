package com.iqac.project.service;

import com.iqac.project.entity.*;
import com.iqac.project.repository.*;
import com.iqac.project.util.AppConstants;
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
public class CurriculumSyllabusService {

    private final CurriculumSyllabusRepository repo;
    private final DepartmentRepository deptRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public CurriculumSyllabusService(CurriculumSyllabusRepository repo, DepartmentRepository deptRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
    }

    public String uploadFile(MultipartFile file, Long deptId,
                             String academicYear, String semester,
                             String regulation) throws IOException {

        if (!AppConstants.PDF_CONTENT_TYPE.equals(file.getContentType()))
            throw new RuntimeException("Only PDF files are allowed");

        if (file.getSize() > AppConstants.MAX_FILE_SIZE)
            throw new RuntimeException("File size exceeds 10MB limit");

        log.info("Uploading syllabus for dept {}, year {}, semester {}", deptId, academicYear, semester);

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        CurriculumSyllabus cs = CurriculumSyllabus.builder()
                .academicYear(academicYear)
                .semester(semester)
                .regulation(regulation)
                .fileName(file.getOriginalFilename())
                .filePath(dest.getAbsolutePath())
                .department(dept)
                .build();

        repo.save(cs);
        log.info("Syllabus uploaded successfully with id {}", cs.getId());
        return cs.getId().toString();
    }

    // ✅ GET LIST
    public List<CurriculumSyllabus> getAll(Long deptId, String year) {
        if (year != null)
            return repo.findByDepartmentIdAndAcademicYear(deptId, year);
        return repo.findByDepartmentId(deptId);
    }

    public CurriculumSyllabus getById(Long id, Long deptId) {

        CurriculumSyllabus cs = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!cs.getDepartment().getId().equals(deptId)) {
            throw new RuntimeException("Unauthorized");
        }

        return cs;
    }

    // ✅ DOWNLOAD

    public File downloadFile(String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }

        return file;
    }

    // ✅ DELETE
    public void delete(Long id, Long deptId) {

        CurriculumSyllabus cs = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!cs.getDepartment().getId().equals(deptId)) {
            throw new RuntimeException("Unauthorized");
        }

        // delete file
        new File(cs.getFilePath()).delete();

        repo.delete(cs);
    }

    public String getFilePath(Long id, Long deptId) {

        CurriculumSyllabus cs = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // ✅ Optional: department check (important for security)
        if (!cs.getDepartment().getId().equals(deptId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return cs.getFilePath();
    }
}
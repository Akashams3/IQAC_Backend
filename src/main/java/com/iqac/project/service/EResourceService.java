package com.iqac.project.service;

import com.iqac.project.entity.Department;
import com.iqac.project.entity.EResource;
import com.iqac.project.entity.Faculty;
import com.iqac.project.entity.enums.ResourceStatus;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.EResourceRepository;
import com.iqac.project.repository.FacultyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class EResourceService {

    private final EResourceRepository repo;
    private final DepartmentRepository deptRepo;
    private final FacultyRepository facultyRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public EResourceService(EResourceRepository repo,
                            DepartmentRepository deptRepo,
                            FacultyRepository facultyRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
        this.facultyRepo = facultyRepo;
    }

    public void create(MultipartFile file,
                       String title, String subject,
                       String academicYear, String className,
                       String type, String link,
                       Long facultyId, Long deptId) throws IOException {

        log.info("Creating e-resource for faculty={}, dept={}, type={}", facultyId, deptId, type);

        String filePath = null;
        String fileName = null;

        if ("FILE".equalsIgnoreCase(type) && file != null && !file.isEmpty()) {
            File dir = new File(uploadDir + "/eresources/");
            if (!dir.exists() && !dir.mkdirs())
                throw new RuntimeException("Failed to create upload directory");

            String safeName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            fileName = UUID.randomUUID() + "_" + safeName;
            File dest = new File(dir, fileName);

            String canonicalDir = dir.getCanonicalPath();
            if (!dest.getCanonicalPath().startsWith(canonicalDir))
                throw new RuntimeException("Invalid file path detected");

            file.transferTo(dest);
            filePath = dest.getAbsolutePath();
            fileName = safeName;
        }

        Faculty faculty = facultyRepo.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        repo.save(EResource.builder()
                .title(title)
                .subject(subject)
                .academicYear(academicYear)
                .className(className)
                .type(type.toUpperCase(Locale.ROOT))
                .link(link)
                .fileName(fileName)
                .filePath(filePath)
                .status(ResourceStatus.DRAFT)
                .faculty(faculty)
                .department(dept)
                .build());

        log.info("E-resource created for faculty={}", facultyId);
    }

    public void submit(Long id, Long deptId) {
        log.info("Submitting e-resource id={}", id);
        EResource r = getResource(id, deptId);
        if (r.getStatus() != ResourceStatus.DRAFT)
            throw new RuntimeException("Only draft can be submitted");
        r.setStatus(ResourceStatus.SUBMITTED);
        repo.save(r);
    }

    public void approve(Long id, Long deptId) {
        log.info("Approving e-resource id={}", id);
        EResource r = getResource(id, deptId);
        if (r.getStatus() != ResourceStatus.SUBMITTED)
            throw new RuntimeException("Only submitted can be approved");
        r.setStatus(ResourceStatus.APPROVED);
        repo.save(r);
    }

    public void reject(Long id, Long deptId) {
        log.info("Rejecting e-resource id={}", id);
        EResource r = getResource(id, deptId);
        if (r.getStatus() != ResourceStatus.SUBMITTED)
            throw new RuntimeException("Only submitted can be rejected");
        r.setStatus(ResourceStatus.REJECTED);
        repo.save(r);
    }

    public List<EResource> getMy(Long facultyId) {
        log.info("Fetching e-resources for faculty={}", facultyId);
        return repo.findByFacultyId(facultyId);
    }

    public EResource getById(Long id, Long deptId) {
        log.info("Fetching e-resource id={} for dept={}", id, deptId);
        return getResource(id, deptId);
    }

    public List<EResource> getAll(Long deptId, String academicYear,
                                  String className, String subject,
                                  String type, String status) {
        log.info("Fetching e-resources for dept={}, year={}, class={}, subject={}, type={}, status={}", deptId, academicYear, className, subject, type, status);
        ResourceStatus statusEnum = (status != null) ? ResourceStatus.valueOf(status.toUpperCase(Locale.ROOT)) : null;
        String typeUpper = (type != null) ? type.toUpperCase(Locale.ROOT) : null;
        return repo.findByFilters(deptId, academicYear, className, subject, typeUpper, statusEnum);
    }

    public void update(Long id, Long facultyId, Long deptId, String title, String subject,
                       String academicYear, String className, String link) {
        log.info("Updating e-resource id={} by faculty={}", id, facultyId);
        EResource r = getResource(id, deptId);
        if (r.getStatus() != ResourceStatus.DRAFT)
            throw new RuntimeException("Only DRAFT resources can be updated");
        if (!r.getFaculty().getId().equals(facultyId))
            throw new RuntimeException("You can only update your own resources");
        r.setTitle(title);
        r.setSubject(subject);
        r.setAcademicYear(academicYear);
        r.setClassName(className);
        r.setLink(link);
        repo.save(r);
        log.info("E-resource id={} updated", id);
    }

    public File download(Long id, Long deptId) {
        log.info("Downloading e-resource id={}", id);
        EResource r = getResource(id, deptId);
        if (!"FILE".equalsIgnoreCase(r.getType()))
            throw new RuntimeException("Not a file resource");
        File file = new File(r.getFilePath());
        if (!file.exists())
            throw new RuntimeException("File not found on disk");
        return file;
    }

    public void delete(Long id, Long facultyId, Long deptId) {
        log.info("Deleting e-resource id={} by faculty={}", id, facultyId);
        EResource r = getResource(id, deptId);
        if (r.getStatus() != ResourceStatus.DRAFT)
            throw new RuntimeException("Only DRAFT resources can be deleted");
        if (!r.getFaculty().getId().equals(facultyId))
            throw new RuntimeException("You can only delete your own resources");
        if (r.getFilePath() != null) {
            File file = new File(r.getFilePath());
            if (!file.delete())
                log.warn("Failed to delete physical file: {}", r.getFilePath());
        }
        repo.delete(r);
        log.info("E-resource id={} deleted", id);
    }

    private EResource getResource(Long id, Long deptId) {
        return repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}

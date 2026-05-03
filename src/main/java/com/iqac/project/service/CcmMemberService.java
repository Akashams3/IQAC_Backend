package com.iqac.project.service;

import com.iqac.project.dto.CcmRequest;
import com.iqac.project.dto.CcmResponse;
import com.iqac.project.entity.CcmMember;
import com.iqac.project.entity.Department;
import com.iqac.project.repository.CcmMemberRepository;
import com.iqac.project.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CcmMemberService {

    private final CcmMemberRepository repo;
    private final DepartmentRepository deptRepo;

    public CcmMemberService(CcmMemberRepository repo, DepartmentRepository deptRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
    }

    public void create(Long deptId, CcmRequest req) {
        log.info("Creating CCM member name={}, class={}, year={}, dept={}", req.getName(), req.getClassName(), req.getAcademicYear(), deptId);
        if (repo.existsByNameAndClassNameAndAcademicYearAndDepartmentId(req.getName(), req.getClassName(), req.getAcademicYear(), deptId))
            throw new RuntimeException("Member already exists for this class & year");
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        repo.save(CcmMember.builder()
                .name(req.getName())
                .role(req.getRole())
                .className(req.getClassName())
                .academicYear(req.getAcademicYear())
                .department(dept)
                .build());
        log.info("CCM member created name={}", req.getName());
    }

    public List<CcmResponse> getAll(Long deptId, String year, String className, String role) {
        log.info("Fetching CCM members for dept={}, year={}, class={}, role={}", deptId, year, className, role);
        return repo.findByFilters(deptId, year, className, role)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public CcmResponse getById(Long id, Long deptId) {
        log.info("Fetching CCM member id={} for dept={}", id, deptId);
        return mapToResponse(repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Member not found")));
    }

    public void update(Long id, Long deptId, CcmRequest req) {
        log.info("Updating CCM member id={}", id);
        CcmMember m = repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        m.setName(req.getName());
        m.setRole(req.getRole());
        m.setClassName(req.getClassName());
        m.setAcademicYear(req.getAcademicYear());
        repo.save(m);
        log.info("CCM member id={} updated", id);
    }

    public void delete(Long id, Long deptId) {
        log.info("Deleting CCM member id={}", id);
        repo.delete(repo.findByIdAndDepartmentId(id, deptId)
                .orElseThrow(() -> new RuntimeException("Member not found")));
    }

    private CcmResponse mapToResponse(CcmMember m) {
        return CcmResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .role(m.getRole())
                .className(m.getClassName())
                .academicYear(m.getAcademicYear())
                .build();
    }
}

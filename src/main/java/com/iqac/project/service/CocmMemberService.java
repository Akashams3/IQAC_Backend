package com.iqac.project.service;

import com.iqac.project.dto.CocmRequest;
import com.iqac.project.entity.CocmMember;
import com.iqac.project.entity.Department;
import com.iqac.project.repository.CocmMemberRepository;
import com.iqac.project.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CocmMemberService {

    private final CocmMemberRepository repo;
    private final DepartmentRepository deptRepo;

    public CocmMemberService(CocmMemberRepository repo, DepartmentRepository deptRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
    }

    public void create(Long deptId, CocmRequest req) {
        log.info("Creating COCM member name={}, year={}, dept={}", req.getName(), req.getAcademicYear(), deptId);
        if (repo.existsByNameAndDepartmentIdAndAcademicYear(req.getName(), deptId, req.getAcademicYear()))
            throw new RuntimeException("Member already exists for this year");
        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        repo.save(CocmMember.builder()
                .name(req.getName())
                .designation(req.getDesignation())
                .role(req.getRole())
                .academicYear(req.getAcademicYear())
                .department(dept)
                .build());
        log.info("COCM member created name={}", req.getName());
    }

    public List<CocmMember> getAll(Long deptId, String academicYear, String role) {
        log.info("Fetching COCM members for dept={}, year={}, role={}", deptId, academicYear, role);
        return repo.findByFilters(deptId, academicYear, role);
    }

    public CocmMember getById(Long id, Long deptId) {
        log.info("Fetching COCM member id={} for dept={}", id, deptId);
        CocmMember m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (!m.getDepartment().getId().equals(deptId))
            throw new RuntimeException("Unauthorized");
        return m;
    }

    public void update(Long id, Long deptId, CocmRequest req) {
        log.info("Updating COCM member id={}", id);
        CocmMember m = getById(id, deptId);
        m.setName(req.getName());
        m.setDesignation(req.getDesignation());
        m.setRole(req.getRole());
        m.setAcademicYear(req.getAcademicYear());
        repo.save(m);
        log.info("COCM member id={} updated", id);
    }

    public void delete(Long id, Long deptId) {
        log.info("Deleting COCM member id={}", id);
        repo.delete(getById(id, deptId));
    }
}

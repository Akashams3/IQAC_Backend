package com.iqac.project.service;

import com.iqac.project.dto.CocmRequest;
import com.iqac.project.entity.CocmMember;
import com.iqac.project.entity.Department;
import com.iqac.project.repository.CocmMemberRepository;
import com.iqac.project.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocmMemberService {

    private final CocmMemberRepository repo;
    private final DepartmentRepository deptRepo;

    public CocmMemberService(CocmMemberRepository repo, DepartmentRepository deptRepo) {
        this.repo = repo;
        this.deptRepo = deptRepo;
    }

    // ✅ CREATE
    public void create(Long deptId, CocmRequest req) {

        if (repo.existsByNameAndDepartmentIdAndAcademicYear(
                req.getName(), deptId, req.getAcademicYear())) {
            throw new RuntimeException("Member already exists for this year");
        }

        Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        CocmMember m = CocmMember.builder()
                .name(req.getName())
                .designation(req.getDesignation())
                .role(req.getRole())
                .academicYear(req.getAcademicYear())
                .department(dept)
                .build();

        repo.save(m);
    }

    public List<CocmMember> getAll(Long deptId, String academicYear, String role) {
        return repo.findByFilters(deptId, academicYear, role);
    }

    // ✅ GET BY ID
    public CocmMember getById(Long id, Long deptId) {
        CocmMember m = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (!m.getDepartment().getId().equals(deptId))
            throw new RuntimeException("Unauthorized");

        return m;
    }

    // ✅ UPDATE
    public void update(Long id, Long deptId, CocmRequest req) {

        CocmMember m = getById(id, deptId);

        m.setName(req.getName());
        m.setDesignation(req.getDesignation());
        m.setRole(req.getRole());
        m.setAcademicYear(req.getAcademicYear());

        repo.save(m);
    }

    // ✅ DELETE
    public void delete(Long id, Long deptId) {

        CocmMember m = getById(id, deptId);
        repo.delete(m);
    }
}
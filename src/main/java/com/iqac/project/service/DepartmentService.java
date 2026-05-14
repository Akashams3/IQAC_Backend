package com.iqac.project.service;

import com.iqac.project.dto.DepartmentDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final MessageSource messageSource;

    public DepartmentService(DepartmentRepository departmentRepository, MessageSource messageSource) {
        this.departmentRepository = departmentRepository;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public List<Department> getAll() {
        log.info("Fetching all departments");
        return departmentRepository.findAll();
    }

    public Department getById(Long id) {
        log.info("Fetching department id={}", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(msg("department.not.found")));
    }

    public Department create(DepartmentDTO dto) {
        log.info("Creating department name={}", dto.getDeptName());
        Department dept = departmentRepository.save(Department.builder().deptName(dto.getDeptName()).build());
        log.info("Department created id={}", dept.getId());
        return dept;
    }

    public Department update(Long id, DepartmentDTO dto) {
        log.info("Updating department id={}", id);
        Department dept = getById(id);
        dept.setDeptName(dto.getDeptName());
        return departmentRepository.save(dept);
    }

    public void delete(Long id) {
        log.info("Deleting department id={}", id);
        departmentRepository.delete(getById(id));
        log.info("Department id={} deleted", id);
    }
}

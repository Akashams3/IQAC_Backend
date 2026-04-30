package com.iqac.project.service;

import com.iqac.project.dto.DepartmentDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.repository.DepartmentRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return departmentRepository.findAll();
    }

    public Department create(DepartmentDTO dto) {
        Department dept = Department.builder().deptName(dto.getDeptName()).build();
        return departmentRepository.save(dept);
    }
}

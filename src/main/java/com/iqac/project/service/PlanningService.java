package com.iqac.project.service;

import com.iqac.project.dto.PlanningDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Planning;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.PlanningRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final DepartmentRepository departmentRepository;
    private final MessageSource messageSource;

    public PlanningService(PlanningRepository planningRepository, DepartmentRepository departmentRepository,
                           MessageSource messageSource) {
        this.planningRepository = planningRepository;
        this.departmentRepository = departmentRepository;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public List<Planning> getAll(Long departmentId) {
        return planningRepository.findByDepartmentId(departmentId);
    }

    public Planning getById(Long id, Long departmentId) {
        Planning planning = planningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(msg("planning.not.found")));
        if (!planning.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException(msg("planning.not.found.in.department"));
        return planning;
    }

    public Planning create(PlanningDTO dto, Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(msg("department.not.found")));
        Planning planning = Planning.builder()
                .planTitle(dto.getPlanTitle())
                .description(dto.getDescription())
                .department(dept)
                .build();
        return planningRepository.save(planning);
    }

    public Planning update(Long id, PlanningDTO dto, Long departmentId) {
        Planning existing = getById(id, departmentId);
        existing.setPlanTitle(dto.getPlanTitle());
        existing.setDescription(dto.getDescription());
        return planningRepository.save(existing);
    }

    public void delete(Long id, Long departmentId) {
        getById(id, departmentId);
        planningRepository.deleteById(id);
    }
}

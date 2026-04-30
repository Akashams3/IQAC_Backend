package com.iqac.project.service;

import com.iqac.project.dto.TeachingScheduleDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.TeachingSchedule;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.TeachingScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingScheduleService {

    private final TeachingScheduleRepository teachingScheduleRepository;
    private final DepartmentRepository departmentRepository;

    public TeachingScheduleService(TeachingScheduleRepository teachingScheduleRepository,
                                   DepartmentRepository departmentRepository) {
        this.teachingScheduleRepository = teachingScheduleRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<TeachingSchedule> getAll(Long departmentId) {
        return teachingScheduleRepository.findByDepartmentId(departmentId);
    }

    public List<TeachingSchedule> getByAcademicYear(Long departmentId, String academicYear) {
        return teachingScheduleRepository.findByDepartmentIdAndAcademicYear(departmentId, academicYear);
    }

    public TeachingSchedule getById(Long id, Long departmentId) {
        TeachingSchedule ts = teachingScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teaching schedule not found"));
        if (!ts.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException("Teaching schedule not found in your department");
        return ts;
    }

    public TeachingSchedule create(TeachingScheduleDTO dto, Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        TeachingSchedule ts = TeachingSchedule.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .date(dto.getDate())
                .priority(dto.getPriority())
                .academicYear(dto.getAcademicYear())
                .department(dept)
                .build();
        return teachingScheduleRepository.save(ts);
    }

    public TeachingSchedule update(Long id, TeachingScheduleDTO dto, Long departmentId) {
        TeachingSchedule existing = getById(id, departmentId);
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setPriority(dto.getPriority());
        existing.setAcademicYear(dto.getAcademicYear());
        return teachingScheduleRepository.save(existing);
    }

    public void delete(Long id, Long departmentId) {
        getById(id, departmentId);
        teachingScheduleRepository.deleteById(id);
    }
}

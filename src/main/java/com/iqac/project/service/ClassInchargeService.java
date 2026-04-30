package com.iqac.project.service;

import com.iqac.project.entity.ClassIncharge;
import com.iqac.project.entity.Department;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.ClassInchargeRepository;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.util.ExcelHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ClassInchargeService {

    private final ClassInchargeRepository classInchargeRepository;
    private final DepartmentRepository departmentRepository;
    private final MessageSource messageSource;

    public ClassInchargeService(ClassInchargeRepository classInchargeRepository,
                                DepartmentRepository departmentRepository,
                                MessageSource messageSource) {
        this.classInchargeRepository = classInchargeRepository;
        this.departmentRepository = departmentRepository;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public List<ClassIncharge> getAll(Long departmentId, String academicYear) {
        if (academicYear != null && !academicYear.isBlank())
            return classInchargeRepository.findByDepartmentIdAndAcademicYear(departmentId, academicYear);
        return classInchargeRepository.findByDepartmentId(departmentId);
    }

    public void uploadExcel(MultipartFile file, Long departmentId, String academicYear) throws IOException {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(msg("department.not.found")));
        List<Map<String, String>> rows = ExcelHelper.parseExcel(file);
        for (Map<String, String> row : rows) {
            ClassIncharge ci = ClassIncharge.builder()
                    .facultyName(row.get("faculty_name"))
                    .className(row.get("class_name"))
                    .section(row.get("section"))
                    .academicYear(academicYear)
                    .department(dept)
                    .build();
            classInchargeRepository.save(ci);
        }
    }

    public void delete(Long id, Long departmentId) {
        ClassIncharge ci = classInchargeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(msg("classincharge.not.found")));
        if (!ci.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException(msg("classincharge.not.found.in.department"));
        classInchargeRepository.deleteById(id);
    }
}

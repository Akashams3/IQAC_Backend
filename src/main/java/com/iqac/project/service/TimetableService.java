package com.iqac.project.service;

import com.iqac.project.entity.Department;
import com.iqac.project.entity.Timetable;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.TimetableRepository;
import com.iqac.project.util.ExcelHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final DepartmentRepository departmentRepository;
    private final MessageSource messageSource;

    public TimetableService(TimetableRepository timetableRepository, DepartmentRepository departmentRepository,
                            MessageSource messageSource) {
        this.timetableRepository = timetableRepository;
        this.departmentRepository = departmentRepository;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public List<Timetable> getAll(Long departmentId, String academicYear) {
        if (academicYear != null && !academicYear.isBlank())
            return timetableRepository.findByDepartmentIdAndAcademicYear(departmentId, academicYear);
        return timetableRepository.findByDepartmentId(departmentId);
    }

    public void uploadExcel(MultipartFile file, Long departmentId, String academicYear) throws IOException {
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(msg("department.not.found")));
        List<Map<String, String>> rows = ExcelHelper.parseExcel(file);
        for (Map<String, String> row : rows) {
            Timetable tt = Timetable.builder()
                    .academicYear(academicYear)
                    .day(row.get("day"))
                    .period(row.get("period"))
                    .subject(row.get("subject"))
                    .facultyName(row.get("faculty_name"))
                    .roomNo(row.get("room_no"))
                    .department(dept)
                    .build();
            timetableRepository.save(tt);
        }
    }

    public void delete(Long id, Long departmentId) {
        Timetable tt = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(msg("timetable.not.found")));
        if (!tt.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException(msg("timetable.not.found.in.department"));
        timetableRepository.deleteById(id);
    }
}

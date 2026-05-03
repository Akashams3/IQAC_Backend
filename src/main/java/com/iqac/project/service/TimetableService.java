package com.iqac.project.service;

import com.iqac.project.entity.Department;
import com.iqac.project.entity.Timetable;
import com.iqac.project.exception.DuplicateResourceException;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.TimetableRepository;
import com.iqac.project.util.ExcelHelper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
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

    public List<Timetable> getAll(Long departmentId, String academicYear, String semester) {
        log.info("Fetching timetable for dept={}, year={}, semester={}", departmentId, academicYear, semester);
        if (academicYear != null && semester != null)
            return timetableRepository.findByDepartmentIdAndAcademicYearAndSemester(departmentId, academicYear, semester);
        if (academicYear != null)
            return timetableRepository.findByDepartmentIdAndAcademicYear(departmentId, academicYear);
        if (semester != null)
            return timetableRepository.findByDepartmentIdAndSemester(departmentId, semester);
        return timetableRepository.findByDepartmentId(departmentId);
    }

    public Timetable getById(Long id, Long deptId) {
        log.info("Fetching timetable id={} for dept={}", id, deptId);
        Timetable tt = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        if (!tt.getDepartment().getId().equals(deptId))
            throw new RuntimeException("Unauthorized");
        return tt;
    }

    public void uploadExcel(MultipartFile file, Long departmentId,
                            String academicYear, String semester) throws IOException {
        log.info("Uploading timetable for dept={}, year={}, semester={}", departmentId, academicYear, semester);

        if (timetableRepository.existsByDepartmentIdAndAcademicYearAndSemester(departmentId, academicYear, semester))
            throw new DuplicateResourceException(msg("timetable.already.exists"));

        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(msg("department.not.found")));

        List<Map<String, String>> rows = ExcelHelper.parseExcel(file);
        for (Map<String, String> row : rows) {
            timetableRepository.save(Timetable.builder()
                    .academicYear(academicYear)
                    .semester(semester)
                    .day(row.get("day"))
                    .period(row.get("period"))
                    .subject(row.get("subject"))
                    .facultyName(row.get("faculty_name"))
                    .roomNo(row.get("room_no"))
                    .department(dept)
                    .build());
        }
        log.info("Timetable uploaded successfully for dept={}, year={}, semester={}", departmentId, academicYear, semester);
    }

    public void updateBySlot(Long deptId, String year, String sem,
                             String day, String period, Timetable req) {
        log.info("Updating timetable slot dept={}, year={}, sem={}, day={}, period={}", deptId, year, sem, day, period);
        Timetable tt = timetableRepository
                .findByDepartmentIdAndAcademicYearAndSemesterAndDayAndPeriod(deptId, year, sem, day, period)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
        tt.setSubject(req.getSubject());
        tt.setFacultyName(req.getFacultyName());
        tt.setRoomNo(req.getRoomNo());
        timetableRepository.save(tt);
    }

    public byte[] download(Long deptId, String academicYear, String semester) throws IOException {
        log.info("Downloading timetable for dept={}, year={}, semester={}", deptId, academicYear, semester);
        List<Timetable> list = timetableRepository
                .findByDepartmentIdAndAcademicYearAndSemester(deptId, academicYear, semester);
        if (list.isEmpty())
            throw new ResourceNotFoundException("No timetable found");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Timetable");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Day");
            header.createCell(1).setCellValue("Period");
            header.createCell(2).setCellValue("Subject");
            header.createCell(3).setCellValue("Faculty");
            header.createCell(4).setCellValue("Room");

            int rowNum = 1;
            for (Timetable tt : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tt.getDay());
                row.createCell(1).setCellValue(tt.getPeriod());
                row.createCell(2).setCellValue(tt.getSubject());
                row.createCell(3).setCellValue(tt.getFacultyName());
                row.createCell(4).setCellValue(tt.getRoomNo());
            }
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public void deleteByFilter(Long deptId, String academicYear, String semester) {
        log.info("Deleting timetable for dept={}, year={}, semester={}", deptId, academicYear, semester);
        if (!timetableRepository.existsByDepartmentIdAndAcademicYearAndSemester(deptId, academicYear, semester))
            throw new ResourceNotFoundException("No data to delete");
        timetableRepository.deleteByDepartmentIdAndAcademicYearAndSemester(deptId, academicYear, semester);
        log.info("Timetable deleted for dept={}, year={}, semester={}", deptId, academicYear, semester);
    }
}

package com.iqac.project.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ExcelHelper {

    public static List<Map<String, String>> parseExcel(MultipartFile file) throws IOException {
        log.info("Parsing Excel file: {}", file.getOriginalFilename());
        List<Map<String, String>> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.warn("Excel file has no header row");
                return result;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow)
                headers.add(cell.toString().trim().toLowerCase(Locale.ROOT));

            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowMap.put(headers.get(j), formatter.formatCellValue(cell).trim());
                }
                result.add(rowMap);
            }
        }

        log.info("Parsed {} rows from Excel file", result.size());
        return result;
    }
}

package com.iqac.project.util;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public class ExcelHelper {

    public static List<Map<String, String>> parseExcel(MultipartFile file) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return result;

        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.toString().trim());
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                rowMap.put(headers.get(j), cell.toString().trim());
            }
            result.add(rowMap);
        }
        workbook.close();
        return result;
    }
}

package com.iqac.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialResponse {
    private Long id;
    private String title;
    private String subject;
    private String academicYear;
    private String semester;
    private String status;
    private String facultyName;
}
package com.iqac.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassInchargeResponse {

    private Long id;
    private String className;
    private String academicYear;

    private Long facultyId;
    private String facultyName;
    private String email;
}
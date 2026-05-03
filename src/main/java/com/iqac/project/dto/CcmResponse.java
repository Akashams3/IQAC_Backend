package com.iqac.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CcmResponse {
    private Long id;
    private String name;
    private String role;
    private String className;
    private String academicYear;
}
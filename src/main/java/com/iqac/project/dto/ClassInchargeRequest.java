package com.iqac.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassInchargeRequest {
    @NotNull
    private Long facultyId;
    @NotBlank
    private String className;
    @NotBlank
    private String academicYear;
}
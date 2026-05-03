package com.iqac.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonPlanRequest {
    @NotBlank
    private String subject;
    @NotBlank
    private String unitName;
    @NotBlank
    private String topic;
    @NotNull
    private Integer plannedHours;
    @NotNull
    private Integer completedHours;
    @NotBlank
    private String academicYear;
    @NotBlank
    private String semester;
}
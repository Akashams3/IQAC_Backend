package com.iqac.project.dto;

import lombok.Data;

@Data
public class LessonPlanRequest {
    private String subject;
    private String unitName;
    private String topic;
    private Integer plannedHours;
    private Integer completedHours;
    private String academicYear;
    private String semester;
}
package com.iqac.project.dto;

import lombok.Data;

@Data
public class ClassMentorRequest {
    private Long facultyId;
    private String className;
    private String academicYear;
}
package com.iqac.project.dto;

import lombok.Data;

@Data
public class EResourceRequest {
    private String title;
    private String subject;
    private String type; // FILE / LINK
    private String link;
    private String academicYear;
    private String className;
}
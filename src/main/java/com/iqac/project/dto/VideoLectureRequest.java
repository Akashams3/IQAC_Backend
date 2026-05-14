package com.iqac.project.dto;

import lombok.Data;

@Data
public class VideoLectureRequest {

    private String title;
    private String subject;
    private String academicYear;
    private String className;
    private String videoUrl;
}
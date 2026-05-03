package com.iqac.project.entity;

import com.iqac.project.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subject;
    private String academicYear;
    private String className;

    private String videoUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Faculty faculty;

    @ManyToOne
    private Department department;
}

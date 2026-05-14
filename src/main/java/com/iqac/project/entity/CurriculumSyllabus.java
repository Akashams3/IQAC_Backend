package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curriculum_syllabus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumSyllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String academicYear;

    private String semester; // ODD / EVEN

    private String regulation; // R2021, R2025 etc

    private String fileName;

    private String filePath; // local path

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
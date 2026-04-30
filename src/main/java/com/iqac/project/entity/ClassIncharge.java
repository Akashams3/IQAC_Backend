package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_incharge")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassIncharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;

    @Column(name = "class_name")
    private String className;

    @Column
    private String section;

    @Column(name = "academic_year")
    private String academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

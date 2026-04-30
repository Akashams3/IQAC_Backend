package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teaching_schedule")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TeachingSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String date;

    @Column
    private String priority;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

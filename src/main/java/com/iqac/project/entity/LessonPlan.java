package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String unitName;
    private String topic;

    private Integer plannedHours;
    private Integer completedHours;

    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    @Column(name = "academic_year")
    private String academicYear;

    private String semester; // ODD / EVEN

    @Column(name = "approval_status")
    private String approvalStatus; // DRAFT, SUBMITTED, APPROVED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
}
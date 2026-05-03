package com.iqac.project.entity;

import com.iqac.project.entity.enums.ApprovalStatus;
import com.iqac.project.entity.enums.LessonPlanStatus;
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

    @Enumerated(EnumType.STRING)
    private LessonPlanStatus status;

    @Column(name = "academic_year")
    private String academicYear;

    private String semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
}
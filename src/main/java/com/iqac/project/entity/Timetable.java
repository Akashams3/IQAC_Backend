package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timetable")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column
    private String day;

    @Column
    private String period;

    @Column
    private String subject;

    @Column(name = "faculty_name")
    private String facultyName;

    @Column(name = "room_no")
    private String roomNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "semester", nullable = false)
    private String semester;
}

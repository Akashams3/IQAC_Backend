package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faculty")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String designation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

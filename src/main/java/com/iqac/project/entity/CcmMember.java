package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ccm_members",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"name", "class_name", "academic_year", "department_id"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CcmMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String role; // Faculty / Student Rep / Coordinator

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
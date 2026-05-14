package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cocm_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CocmMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String designation;

    private String role;

    private String academicYear;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
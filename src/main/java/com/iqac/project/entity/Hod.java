package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hod")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Hod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hod_name", nullable = false)
    private String hodName;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

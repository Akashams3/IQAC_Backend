package com.iqac.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "iqac_coordinator")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IqacCoordinator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coordinator_name", nullable = false)
    private String coordinatorName;

    @Column(nullable = false, unique = true)
    private String email;
}

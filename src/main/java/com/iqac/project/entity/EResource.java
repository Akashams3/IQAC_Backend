package com.iqac.project.entity;

import com.iqac.project.entity.enums.ResourceStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "e_resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subject;
    private String type; // FILE or LINK

    private String filePath;
    private String fileName;

    private String link;

    private String academicYear;
    private String className;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
package com.iqac.project.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "class_incharge",
        uniqueConstraints = {

                // ✅ One class → one incharge
                @UniqueConstraint(columnNames = {"class_name", "academic_year"}),

                // ✅ One faculty → one class
                @UniqueConstraint(columnNames = {"faculty_id", "academic_year"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassIncharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
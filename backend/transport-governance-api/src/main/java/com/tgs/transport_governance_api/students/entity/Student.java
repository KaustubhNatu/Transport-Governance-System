package com.tgs.transport_governance_api.students.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "school_id", nullable = false)
    private Integer schoolId;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Column(nullable = false)
    private String name;

    private String grade;

    private String section;

    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
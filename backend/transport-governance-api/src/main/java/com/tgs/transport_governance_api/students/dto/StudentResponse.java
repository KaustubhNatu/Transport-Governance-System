package com.tgs.transport_governance_api.students.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentResponse {

    private Integer id;

    private Integer schoolId;

    private Integer parentId;

    private String name;

    private String grade;

    private String section;

    private String status;

    private LocalDateTime createdAt;
}
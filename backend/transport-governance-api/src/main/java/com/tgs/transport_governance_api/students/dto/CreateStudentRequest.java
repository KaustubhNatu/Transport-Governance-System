package com.tgs.transport_governance_api.students.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudentRequest {

    @NotNull
    private Integer schoolId;

    @NotNull
    private Integer parentId;

    @NotBlank
    private String name;

    private String grade;

    private String section;
}
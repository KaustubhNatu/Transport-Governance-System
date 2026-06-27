package com.tgs.transport_governance_api.students.controller;

import com.tgs.transport_governance_api.students.dto.CreateStudentRequest;
import com.tgs.transport_governance_api.students.dto.StudentResponse;
import com.tgs.transport_governance_api.students.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public StudentResponse createStudent(
            @RequestBody @Valid CreateStudentRequest request) {

        return studentService.createStudent(request);
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {

        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(
            @PathVariable Integer id) {

        return studentService.getStudentById(id);
    }
}
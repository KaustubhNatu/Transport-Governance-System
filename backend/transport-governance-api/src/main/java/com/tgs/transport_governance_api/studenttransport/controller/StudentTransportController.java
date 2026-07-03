package com.tgs.transport_governance_api.studenttransport.controller;

import com.tgs.transport_governance_api.studenttransport.dto.CreateStudentTransportRequest;
import com.tgs.transport_governance_api.studenttransport.dto.StudentTransportResponse;
import com.tgs.transport_governance_api.studenttransport.service.StudentTransportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-transport")
public class StudentTransportController {

    @Autowired
    private StudentTransportService studentTransportService;

    @PostMapping
    public StudentTransportResponse createStudentTransport(
            @RequestBody @Valid
            CreateStudentTransportRequest request) {

        return studentTransportService
                .createStudentTransport(request);
    }

    @GetMapping
    public List<StudentTransportResponse> getAllStudentTransport() {

        return studentTransportService
                .getAllStudentTransport();
    }

    @GetMapping("/{id}")
    public StudentTransportResponse getStudentTransportById(
            @PathVariable Integer id) {

        return studentTransportService
                .getStudentTransportById(id);
    }
}
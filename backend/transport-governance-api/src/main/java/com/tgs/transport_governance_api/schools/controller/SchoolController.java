package com.tgs.transport_governance_api.schools.controller;

import com.tgs.transport_governance_api.schools.dto.CreateSchoolRequest;
import com.tgs.transport_governance_api.schools.dto.SchoolResponse;
import com.tgs.transport_governance_api.schools.service.SchoolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping
    public SchoolResponse createSchool(
            @RequestBody @Valid CreateSchoolRequest request) {

        return schoolService.createSchool(request);
    }

    @GetMapping
    public List<SchoolResponse> getAllSchools() {
        return schoolService.getAllSchools();
    }

    @GetMapping("/{id}")
    public SchoolResponse getSchoolById(
            @PathVariable Integer id) {

        return schoolService.getSchoolById(id);
    }

    @PutMapping("/{id}")
    public SchoolResponse updateSchool(
            @PathVariable Integer id,
            @RequestBody @Valid CreateSchoolRequest request) {

        return schoolService.updateSchool(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSchool(
            @PathVariable Integer id) {

        schoolService.deleteSchool(id);
    }
}
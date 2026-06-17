package com.tgs.transport_governance_api.schools.service;

import com.tgs.transport_governance_api.schools.dto.CreateSchoolRequest;
import com.tgs.transport_governance_api.schools.dto.SchoolResponse;
import com.tgs.transport_governance_api.schools.entity.School;
import com.tgs.transport_governance_api.schools.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public SchoolResponse createSchool(CreateSchoolRequest request) {

        School school = new School();

        school.setName(request.getName());
        school.setAddress(request.getAddress());
        school.setContactEmail(request.getContactEmail());
        school.setContactPhone(request.getContactPhone());
        school.setCreatedAt(LocalDateTime.now());

        School savedSchool = schoolRepository.save(school);

        return mapToResponse(savedSchool);
    }

    private SchoolResponse mapToResponse(School school) {

        SchoolResponse response = new SchoolResponse();

        response.setId(school.getId());
        response.setName(school.getName());
        response.setAddress(school.getAddress());
        response.setContactEmail(school.getContactEmail());
        response.setContactPhone(school.getContactPhone());
        response.setCreatedAt(school.getCreatedAt());

        return response;
    }

    public List<SchoolResponse> getAllSchools() {
        return schoolRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SchoolResponse getSchoolById(Integer id) {

        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("School not found"));

        return mapToResponse(school);
    }

    public SchoolResponse updateSchool(
            Integer id,
            CreateSchoolRequest request) {

        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("School not found"));

        school.setName(request.getName());
        school.setAddress(request.getAddress());
        school.setContactEmail(request.getContactEmail());
        school.setContactPhone(request.getContactPhone());

        School updatedSchool =
                schoolRepository.save(school);

        return mapToResponse(updatedSchool);
    }

    public void deleteSchool(Integer id) {

        School school = schoolRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("School not found"));

        schoolRepository.delete(school);
    }
}
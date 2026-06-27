package com.tgs.transport_governance_api.parents.service;

import com.tgs.transport_governance_api.parents.dto.CreateParentRequest;
import com.tgs.transport_governance_api.parents.dto.ParentResponse;
import com.tgs.transport_governance_api.parents.entity.Parent;
import com.tgs.transport_governance_api.parents.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public ParentResponse createParent(
            CreateParentRequest request) {

        if (parentRepository
                .findByUserId(request.getUserId())
                .isPresent()) {

            throw new RuntimeException(
                    "Parent already exists");
        }

        Parent parent = new Parent();

        parent.setUserId(request.getUserId());
        parent.setAddress(request.getAddress());
        parent.setEmergencyContact(
                request.getEmergencyContact());
        parent.setCreatedAt(LocalDateTime.now());

        Parent savedParent =
                parentRepository.save(parent);

        return mapToResponse(savedParent);
    }

    public List<ParentResponse> getAllParents() {

        return parentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ParentResponse getParentById(Integer id) {

        Parent parent = parentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Parent not found"));

        return mapToResponse(parent);
    }

    private ParentResponse mapToResponse(
            Parent parent) {

        ParentResponse response =
                new ParentResponse();

        response.setId(parent.getId());
        response.setUserId(parent.getUserId());
        response.setAddress(parent.getAddress());
        response.setEmergencyContact(
                parent.getEmergencyContact());
        response.setCreatedAt(parent.getCreatedAt());

        return response;
    }
}
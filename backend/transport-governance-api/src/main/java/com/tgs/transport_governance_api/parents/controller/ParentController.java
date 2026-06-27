package com.tgs.transport_governance_api.parents.controller;

import com.tgs.transport_governance_api.parents.dto.CreateParentRequest;
import com.tgs.transport_governance_api.parents.dto.ParentResponse;
import com.tgs.transport_governance_api.parents.service.ParentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @PostMapping
    public ParentResponse createParent(
            @RequestBody @Valid
            CreateParentRequest request) {

        return parentService.createParent(request);
    }

    @GetMapping
    public List<ParentResponse> getAllParents() {

        return parentService.getAllParents();
    }

    @GetMapping("/{id}")
    public ParentResponse getParentById(
            @PathVariable Integer id) {

        return parentService.getParentById(id);
    }
}
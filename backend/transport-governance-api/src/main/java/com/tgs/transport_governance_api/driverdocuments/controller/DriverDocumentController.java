package com.tgs.transport_governance_api.driverdocuments.controller;

import com.tgs.transport_governance_api.driverdocuments.dto.CreateDriverDocumentRequest;
import com.tgs.transport_governance_api.driverdocuments.dto.DriverDocumentResponse;
import com.tgs.transport_governance_api.driverdocuments.service.DriverDocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver-documents")
public class DriverDocumentController {

    @Autowired
    private DriverDocumentService driverDocumentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDocumentResponse createDocument(
            @RequestBody @Valid CreateDriverDocumentRequest request) {

        return driverDocumentService.createDocument(request);
    }

    @GetMapping
    public List<DriverDocumentResponse> getAllDocuments() {

        return driverDocumentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public DriverDocumentResponse getDocumentById(
            @PathVariable Integer id) {

        return driverDocumentService.getDocumentById(id);
    }

    @PutMapping("/{id}")
    public DriverDocumentResponse updateDocument(
            @PathVariable Integer id,
            @RequestBody @Valid CreateDriverDocumentRequest request) {

        return driverDocumentService.updateDocument(id, request);
    }

}
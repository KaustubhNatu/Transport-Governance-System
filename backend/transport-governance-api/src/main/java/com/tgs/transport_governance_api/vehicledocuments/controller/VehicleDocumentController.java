package com.tgs.transport_governance_api.vehicledocuments.controller;

import com.tgs.transport_governance_api.vehicledocuments.dto.CreateVehicleDocumentRequest;
import com.tgs.transport_governance_api.vehicledocuments.dto.VehicleDocumentResponse;
import com.tgs.transport_governance_api.vehicledocuments.service.VehicleDocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-documents")
public class VehicleDocumentController {

    @Autowired
    private VehicleDocumentService vehicleDocumentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDocumentResponse createDocument(
            @RequestBody @Valid CreateVehicleDocumentRequest request) {

        return vehicleDocumentService.createDocument(request);
    }

    @GetMapping
    public List<VehicleDocumentResponse> getAllDocuments() {

        return vehicleDocumentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public VehicleDocumentResponse getDocumentById(
            @PathVariable Integer id) {

        return vehicleDocumentService.getDocumentById(id);
    }

    @PutMapping("/{id}")
    public VehicleDocumentResponse updateDocument(
            @PathVariable Integer id,
            @RequestBody @Valid CreateVehicleDocumentRequest request) {

        return vehicleDocumentService.updateDocument(id, request);
    }
}
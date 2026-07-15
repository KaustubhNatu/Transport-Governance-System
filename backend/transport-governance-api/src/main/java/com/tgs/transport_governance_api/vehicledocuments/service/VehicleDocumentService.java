package com.tgs.transport_governance_api.vehicledocuments.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.vehicledocuments.dto.CreateVehicleDocumentRequest;
import com.tgs.transport_governance_api.vehicledocuments.dto.VehicleDocumentResponse;
import com.tgs.transport_governance_api.vehicledocuments.entity.VehicleDocument;
import com.tgs.transport_governance_api.vehicledocuments.repository.VehicleDocumentRepository;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleDocumentService {

    @Autowired
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public VehicleDocumentResponse createDocument(
            CreateVehicleDocumentRequest request) {

        vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle not found"));

        if (vehicleDocumentRepository
                .existsByVehicleIdAndDocumentType(
                        request.getVehicleId(),
                        request.getDocumentType())) {

            throw new BadRequestException(
                    "Vehicle already has this document type.");
        }

        VehicleDocument document = new VehicleDocument();

        document.setVehicleId(request.getVehicleId());
        document.setDocumentType(request.getDocumentType());
        document.setDocumentUrl(request.getDocumentUrl());
        document.setExpiryDate(request.getExpiryDate());
        document.setUploadedAt(LocalDateTime.now());

        VehicleDocument saved =
                vehicleDocumentRepository.save(document);

        return mapToResponse(saved);
    }

    public List<VehicleDocumentResponse> getAllDocuments() {

        return vehicleDocumentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public VehicleDocumentResponse getDocumentById(
            Integer id) {

        VehicleDocument document =
                vehicleDocumentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        return mapToResponse(document);
    }

    public VehicleDocumentResponse updateDocument(
            Integer id,
            CreateVehicleDocumentRequest request) {

        VehicleDocument document =
                vehicleDocumentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));



        vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Vehicle not found"));

        if (!document.getDocumentType().equals(request.getDocumentType())
                && vehicleDocumentRepository.existsByVehicleIdAndDocumentType(
                request.getVehicleId(),
                request.getDocumentType())) {

            throw new BadRequestException(
                    "Vehicle already has this document type.");
        }

        document.setVehicleId(request.getVehicleId());
        document.setDocumentType(request.getDocumentType());
        document.setDocumentUrl(request.getDocumentUrl());
        document.setExpiryDate(request.getExpiryDate());

        VehicleDocument updated =
                vehicleDocumentRepository.save(document);

        return mapToResponse(updated);
    }

    private VehicleDocumentResponse mapToResponse(
            VehicleDocument document) {

        VehicleDocumentResponse response =
                new VehicleDocumentResponse();

        response.setId(document.getId());
        response.setVehicleId(document.getVehicleId());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentUrl(document.getDocumentUrl());
        response.setExpiryDate(document.getExpiryDate());
        response.setUploadedAt(document.getUploadedAt());

        return response;
    }
}
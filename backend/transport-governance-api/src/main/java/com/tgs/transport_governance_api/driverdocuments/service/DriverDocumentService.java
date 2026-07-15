package com.tgs.transport_governance_api.driverdocuments.service;

import com.tgs.transport_governance_api.driverdocuments.dto.CreateDriverDocumentRequest;
import com.tgs.transport_governance_api.driverdocuments.dto.DriverDocumentResponse;
import com.tgs.transport_governance_api.driverdocuments.entity.DriverDocument;
import com.tgs.transport_governance_api.driverdocuments.repository.DriverDocumentRepository;
import com.tgs.transport_governance_api.drivers.repository.DriverRepository;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverDocumentService {

    @Autowired
    private DriverDocumentRepository driverDocumentRepository;

    @Autowired
    private DriverRepository driverRepository;

    public DriverDocumentResponse createDocument(
            CreateDriverDocumentRequest request) {

        driverRepository.findById(request.getDriverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver not found"));

        if (driverDocumentRepository
                .existsByDriverIdAndDocumentType(
                        request.getDriverId(),
                        request.getDocumentType())) {

            throw new BadRequestException(
                    "Driver already has this document type.");
        }

        DriverDocument document =
                new DriverDocument();

        document.setDriverId(request.getDriverId());
        document.setDocumentType(request.getDocumentType());
        document.setDocumentUrl(request.getDocumentUrl());
        document.setExpiryDate(request.getExpiryDate());
        document.setUploadedAt(LocalDateTime.now());

        DriverDocument saved =
                driverDocumentRepository.save(document);

        return mapToResponse(saved);
    }

    public List<DriverDocumentResponse> getAllDocuments() {

        return driverDocumentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DriverDocumentResponse getDocumentById(
            Integer id) {

        DriverDocument document =
                driverDocumentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        return mapToResponse(document);
    }

    public DriverDocumentResponse updateDocument(
            Integer id,
            CreateDriverDocumentRequest request) {

        DriverDocument document =
                driverDocumentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Document not found"));

        driverRepository.findById(request.getDriverId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Driver not found"));

        if (!document.getDocumentType().equals(request.getDocumentType())
                && driverDocumentRepository.existsByDriverIdAndDocumentType(
                request.getDriverId(),
                request.getDocumentType())) {

            throw new BadRequestException(
                    "Driver already has this document type.");
        }

        document.setDriverId(request.getDriverId());
        document.setDocumentType(request.getDocumentType());
        document.setDocumentUrl(request.getDocumentUrl());
        document.setExpiryDate(request.getExpiryDate());

        DriverDocument updated =
                driverDocumentRepository.save(document);

        return mapToResponse(updated);
    }

    private DriverDocumentResponse mapToResponse(
            DriverDocument document) {

        DriverDocumentResponse response =
                new DriverDocumentResponse();

        response.setId(document.getId());
        response.setDriverId(document.getDriverId());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentUrl(document.getDocumentUrl());
        response.setExpiryDate(document.getExpiryDate());
        response.setUploadedAt(document.getUploadedAt());

        return response;
    }

}
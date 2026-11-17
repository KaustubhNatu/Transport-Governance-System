package com.tgs.transport_governance_api.vehicledocuments.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.vehicledocuments.dto.CreateVehicleDocumentRequest;
import com.tgs.transport_governance_api.vehicledocuments.dto.VehicleDocumentResponse;
import com.tgs.transport_governance_api.vehicledocuments.entity.VehicleDocument;
import com.tgs.transport_governance_api.vehicledocuments.repository.VehicleDocumentRepository;
import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleDocumentServiceTest {

    @Mock
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleDocumentService vehicleDocumentService;

    @Test
    @DisplayName("Should create vehicle document successfully when vehicle exists and document type is unique")
    void createDocument_success() {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/insurance.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(1));

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(1, "INSURANCE")).thenReturn(false);
        when(vehicleDocumentRepository.save(any(VehicleDocument.class))).thenAnswer(i -> {
            VehicleDocument doc = i.getArgument(0);
            doc.setId(10);
            return doc;
        });

        VehicleDocumentResponse response = vehicleDocumentService.createDocument(request);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals(1, response.getVehicleId());
        assertEquals("INSURANCE", response.getDocumentType());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle does not exist on create")
    void createDocument_vehicleNotFound() {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(999);

        when(vehicleRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleDocumentService.createDocument(request));
        verify(vehicleDocumentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when document type already exists for vehicle on create")
    void createDocument_duplicateDocumentType() {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(new Vehicle()));
        when(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(1, "INSURANCE")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vehicleDocumentService.createDocument(request));
        verify(vehicleDocumentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all vehicle documents successfully")
    void getAllDocuments_success() {
        VehicleDocument doc = new VehicleDocument();
        doc.setId(1);
        doc.setVehicleId(1);
        doc.setDocumentType("INSURANCE");

        when(vehicleDocumentRepository.findAll()).thenReturn(List.of(doc));

        List<VehicleDocumentResponse> responses = vehicleDocumentService.getAllDocuments();

        assertEquals(1, responses.size());
        assertEquals("INSURANCE", responses.get(0).getDocumentType());
    }

    @Test
    @DisplayName("Should get vehicle document by ID successfully")
    void getDocumentById_success() {
        VehicleDocument doc = new VehicleDocument();
        doc.setId(1);
        doc.setVehicleId(1);
        doc.setDocumentType("INSURANCE");

        when(vehicleDocumentRepository.findById(1)).thenReturn(Optional.of(doc));

        VehicleDocumentResponse response = vehicleDocumentService.getDocumentById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when document ID not found")
    void getDocumentById_notFound() {
        when(vehicleDocumentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleDocumentService.getDocumentById(999));
    }

    @Test
    @DisplayName("Should update vehicle document successfully")
    void updateDocument_success() {
        VehicleDocument existing = new VehicleDocument();
        existing.setId(1);
        existing.setVehicleId(1);
        existing.setDocumentType("INSURANCE");

        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/new-insurance.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(2));

        when(vehicleDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(new Vehicle()));
        when(vehicleDocumentRepository.save(any(VehicleDocument.class))).thenAnswer(i -> i.getArgument(0));

        VehicleDocumentResponse response = vehicleDocumentService.updateDocument(1, request);

        assertEquals("https://example.com/new-insurance.pdf", response.getDocumentUrl());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when document not found on update")
    void updateDocument_notFound() {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        when(vehicleDocumentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleDocumentService.updateDocument(999, request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle not found on update")
    void updateDocument_vehicleNotFound() {
        VehicleDocument existing = new VehicleDocument();
        existing.setId(1);
        existing.setVehicleId(1);
        existing.setDocumentType("INSURANCE");

        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(999);

        when(vehicleDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(vehicleRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleDocumentService.updateDocument(1, request));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating to an already existing document type")
    void updateDocument_duplicateDocumentType() {
        VehicleDocument existing = new VehicleDocument();
        existing.setId(1);
        existing.setVehicleId(1);
        existing.setDocumentType("INSURANCE");

        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("FITNESS_CERTIFICATE");

        when(vehicleDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(new Vehicle()));
        when(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(1, "FITNESS_CERTIFICATE")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vehicleDocumentService.updateDocument(1, request));
    }
}


package com.tgs.transport_governance_api.driverdocuments.service;

import com.tgs.transport_governance_api.driverdocuments.dto.CreateDriverDocumentRequest;
import com.tgs.transport_governance_api.driverdocuments.dto.DriverDocumentResponse;
import com.tgs.transport_governance_api.driverdocuments.entity.DriverDocument;
import com.tgs.transport_governance_api.driverdocuments.repository.DriverDocumentRepository;
import com.tgs.transport_governance_api.drivers.entity.Driver;
import com.tgs.transport_governance_api.drivers.repository.DriverRepository;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverDocumentServiceTest {

    @Mock
    private DriverDocumentRepository driverDocumentRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverDocumentService driverDocumentService;

    @Test
    @DisplayName("Should create driver document successfully when driver exists and document type is unique")
    void createDocument_success() {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/license.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(1));

        Driver driver = new Driver();
        driver.setId(1);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverDocumentRepository.existsByDriverIdAndDocumentType(1, "LICENSE")).thenReturn(false);
        when(driverDocumentRepository.save(any(DriverDocument.class))).thenAnswer(i -> {
            DriverDocument doc = i.getArgument(0);
            doc.setId(10);
            return doc;
        });

        DriverDocumentResponse response = driverDocumentService.createDocument(request);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals(1, response.getDriverId());
        assertEquals("LICENSE", response.getDocumentType());
        assertEquals("https://example.com/license.pdf", response.getDocumentUrl());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when driver does not exist on create")
    void createDocument_driverNotFound() {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(999);

        when(driverRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverDocumentService.createDocument(request));
        verify(driverDocumentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when document type already exists for driver on create")
    void createDocument_duplicateDocumentType() {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");

        when(driverRepository.findById(1)).thenReturn(Optional.of(new Driver()));
        when(driverDocumentRepository.existsByDriverIdAndDocumentType(1, "LICENSE")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> driverDocumentService.createDocument(request));
        verify(driverDocumentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all driver documents successfully")
    void getAllDocuments_success() {
        DriverDocument doc = new DriverDocument();
        doc.setId(1);
        doc.setDriverId(1);
        doc.setDocumentType("LICENSE");

        when(driverDocumentRepository.findAll()).thenReturn(List.of(doc));

        List<DriverDocumentResponse> responses = driverDocumentService.getAllDocuments();

        assertEquals(1, responses.size());
        assertEquals("LICENSE", responses.get(0).getDocumentType());
    }

    @Test
    @DisplayName("Should get driver document by ID successfully")
    void getDocumentById_success() {
        DriverDocument doc = new DriverDocument();
        doc.setId(1);
        doc.setDriverId(1);
        doc.setDocumentType("LICENSE");

        when(driverDocumentRepository.findById(1)).thenReturn(Optional.of(doc));

        DriverDocumentResponse response = driverDocumentService.getDocumentById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when document ID not found")
    void getDocumentById_notFound() {
        when(driverDocumentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverDocumentService.getDocumentById(999));
    }

    @Test
    @DisplayName("Should update driver document successfully")
    void updateDocument_success() {
        DriverDocument existing = new DriverDocument();
        existing.setId(1);
        existing.setDriverId(1);
        existing.setDocumentType("LICENSE");

        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/new-license.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(2));

        when(driverDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(driverRepository.findById(1)).thenReturn(Optional.of(new Driver()));
        when(driverDocumentRepository.save(any(DriverDocument.class))).thenAnswer(i -> i.getArgument(0));

        DriverDocumentResponse response = driverDocumentService.updateDocument(1, request);

        assertEquals("https://example.com/new-license.pdf", response.getDocumentUrl());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when document not found on update")
    void updateDocument_notFound() {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        when(driverDocumentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverDocumentService.updateDocument(999, request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when driver not found on update")
    void updateDocument_driverNotFound() {
        DriverDocument existing = new DriverDocument();
        existing.setId(1);
        existing.setDriverId(1);
        existing.setDocumentType("LICENSE");

        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(999);

        when(driverDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(driverRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverDocumentService.updateDocument(1, request));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating to an already existing document type")
    void updateDocument_duplicateDocumentType() {
        DriverDocument existing = new DriverDocument();
        existing.setId(1);
        existing.setDriverId(1);
        existing.setDocumentType("LICENSE");

        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("POLICE_VERIFICATION");

        when(driverDocumentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(driverRepository.findById(1)).thenReturn(Optional.of(new Driver()));
        when(driverDocumentRepository.existsByDriverIdAndDocumentType(1, "POLICE_VERIFICATION")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> driverDocumentService.updateDocument(1, request));
    }
}


package com.tgs.transport_governance_api.vehicledocuments.repository;

import com.tgs.transport_governance_api.vehicledocuments.entity.VehicleDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VehicleDocumentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Test
    @DisplayName("Should find vehicle documents by vehicle ID")
    void findByVehicleId() {
        VehicleDocument doc1 = new VehicleDocument();
        doc1.setVehicleId(10);
        doc1.setDocumentType("INSURANCE");
        doc1.setDocumentUrl("https://example.com/ins.pdf");
        doc1.setExpiryDate(LocalDate.now().plusYears(1));
        doc1.setUploadedAt(LocalDateTime.now());
        entityManager.persist(doc1);

        VehicleDocument doc2 = new VehicleDocument();
        doc2.setVehicleId(10);
        doc2.setDocumentType("FITNESS_CERTIFICATE");
        doc2.setDocumentUrl("https://example.com/fit.pdf");
        doc2.setExpiryDate(LocalDate.now().plusYears(1));
        doc2.setUploadedAt(LocalDateTime.now());
        entityManager.persist(doc2);

        VehicleDocument other = new VehicleDocument();
        other.setVehicleId(20);
        other.setDocumentType("INSURANCE");
        other.setDocumentUrl("https://example.com/other.pdf");
        entityManager.persist(other);

        entityManager.flush();

        List<VehicleDocument> docs = vehicleDocumentRepository.findByVehicleId(10);
        assertEquals(2, docs.size());
    }

    @Test
    @DisplayName("Should check whether document type exists for a given vehicle ID")
    void existsByVehicleIdAndDocumentType() {
        VehicleDocument doc = new VehicleDocument();
        doc.setVehicleId(10);
        doc.setDocumentType("INSURANCE");
        doc.setDocumentUrl("https://example.com/ins.pdf");
        entityManager.persistAndFlush(doc);

        assertTrue(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(10, "INSURANCE"));
        assertFalse(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(10, "POLLUTION"));
        assertFalse(vehicleDocumentRepository.existsByVehicleIdAndDocumentType(999, "INSURANCE"));
    }
}


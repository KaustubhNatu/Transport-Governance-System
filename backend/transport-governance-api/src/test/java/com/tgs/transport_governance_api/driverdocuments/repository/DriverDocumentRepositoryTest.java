package com.tgs.transport_governance_api.driverdocuments.repository;

import com.tgs.transport_governance_api.driverdocuments.entity.DriverDocument;
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
class DriverDocumentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DriverDocumentRepository driverDocumentRepository;

    @Test
    @DisplayName("Should find driver documents by driver ID")
    void findByDriverId() {
        DriverDocument doc1 = new DriverDocument();
        doc1.setDriverId(10);
        doc1.setDocumentType("LICENSE");
        doc1.setDocumentUrl("https://example.com/license.pdf");
        doc1.setExpiryDate(LocalDate.now().plusYears(1));
        doc1.setUploadedAt(LocalDateTime.now());
        entityManager.persist(doc1);

        DriverDocument doc2 = new DriverDocument();
        doc2.setDriverId(10);
        doc2.setDocumentType("POLICE_VERIFICATION");
        doc2.setDocumentUrl("https://example.com/police.pdf");
        doc2.setExpiryDate(LocalDate.now().plusYears(1));
        doc2.setUploadedAt(LocalDateTime.now());
        entityManager.persist(doc2);

        DriverDocument other = new DriverDocument();
        other.setDriverId(20);
        other.setDocumentType("LICENSE");
        other.setDocumentUrl("https://example.com/other.pdf");
        entityManager.persist(other);

        entityManager.flush();

        List<DriverDocument> docs = driverDocumentRepository.findByDriverId(10);
        assertEquals(2, docs.size());
    }

    @Test
    @DisplayName("Should check whether document type exists for a given driver ID")
    void existsByDriverIdAndDocumentType() {
        DriverDocument doc = new DriverDocument();
        doc.setDriverId(10);
        doc.setDocumentType("LICENSE");
        doc.setDocumentUrl("https://example.com/license.pdf");
        entityManager.persistAndFlush(doc);

        assertTrue(driverDocumentRepository.existsByDriverIdAndDocumentType(10, "LICENSE"));
        assertFalse(driverDocumentRepository.existsByDriverIdAndDocumentType(10, "AADHAAR"));
        assertFalse(driverDocumentRepository.existsByDriverIdAndDocumentType(999, "LICENSE"));
    }
}


package com.tgs.transport_governance_api.transfernotification.repository;

import com.tgs.transport_governance_api.transfernotification.entity.TransferNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransferNotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransferNotificationRepository notificationRepository;

    @Test
    @DisplayName("Should verify active transfer notification existence by student ID")
    void existsByStudentIdAndCompletedAtIsNull() {
        TransferNotification active = new TransferNotification();
        active.setStudentId(100);
        active.setVendorId(200);
        active.setCreatedBy(1);
        active.setCreatedAt(LocalDateTime.now());
        active.setDuesStatus("UNKNOWN");
        active.setDuesAmount(BigDecimal.ZERO);
        entityManager.persistAndFlush(active);

        assertTrue(notificationRepository.existsByStudentIdAndCompletedAtIsNull(100));

        active.setCompletedAt(LocalDateTime.now());
        entityManager.persistAndFlush(active);

        assertFalse(notificationRepository.existsByStudentIdAndCompletedAtIsNull(100));
        assertFalse(notificationRepository.existsByStudentIdAndCompletedAtIsNull(999));
    }

    @Test
    @DisplayName("Should find all notifications by vendor ID")
    void findByVendorId() {
        TransferNotification n1 = new TransferNotification();
        n1.setStudentId(1);
        n1.setVendorId(50);
        n1.setCreatedBy(1);
        entityManager.persist(n1);

        TransferNotification n2 = new TransferNotification();
        n2.setStudentId(2);
        n2.setVendorId(50);
        n2.setCreatedBy(1);
        entityManager.persist(n2);

        TransferNotification n3 = new TransferNotification();
        n3.setStudentId(3);
        n3.setVendorId(99);
        n3.setCreatedBy(1);
        entityManager.persist(n3);

        entityManager.flush();

        List<TransferNotification> results = notificationRepository.findByVendorId(50);
        assertEquals(2, results.size());
    }
}

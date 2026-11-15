package com.tgs.transport_governance_api.transfernotification.repository;

import com.tgs.transport_governance_api.transfernotification.entity.TransferEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TransferEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransferEventRepository eventRepository;

    @Test
    @DisplayName("Should find events by notification ID ordered by createdAt ascending")
    void findByNotificationIdOrderByCreatedAtAsc() {
        TransferEvent e1 = new TransferEvent();
        e1.setNotificationId(10);
        e1.setEventType("NOTIFICATION_CREATED");
        e1.setNotes("First");
        e1.setPerformedBy(1);
        e1.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        entityManager.persist(e1);

        TransferEvent e2 = new TransferEvent();
        e2.setNotificationId(10);
        e2.setEventType("DUES_UPDATED");
        e2.setNotes("Second");
        e2.setPerformedBy(2);
        e2.setCreatedAt(LocalDateTime.of(2026, 1, 1, 11, 0));
        entityManager.persist(e2);

        TransferEvent other = new TransferEvent();
        other.setNotificationId(99);
        other.setEventType("NOTIFICATION_CREATED");
        other.setNotes("Other");
        other.setPerformedBy(1);
        other.setCreatedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
        entityManager.persist(other);

        entityManager.flush();

        List<TransferEvent> events = eventRepository.findByNotificationIdOrderByCreatedAtAsc(10);

        assertEquals(2, events.size());
        assertEquals("First", events.get(0).getNotes());
        assertEquals("Second", events.get(1).getNotes());
    }
}

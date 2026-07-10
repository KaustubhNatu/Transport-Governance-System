package com.tgs.transport_governance_api.transfernotification.repository;

import com.tgs.transport_governance_api.transfernotification.entity.TransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferEventRepository
        extends JpaRepository<TransferEvent, Integer> {

    List<TransferEvent> findByNotificationIdOrderByCreatedAtAsc(
            Integer notificationId);
}
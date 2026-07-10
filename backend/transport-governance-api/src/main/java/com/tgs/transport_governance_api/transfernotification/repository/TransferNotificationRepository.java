package com.tgs.transport_governance_api.transfernotification.repository;

import com.tgs.transport_governance_api.transfernotification.entity.TransferNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferNotificationRepository
        extends JpaRepository<TransferNotification, Integer> {

    boolean existsByStudentIdAndCompletedAtIsNull(
            Integer studentId);

    List<TransferNotification> findByVendorId(
            Integer vendorId);
}
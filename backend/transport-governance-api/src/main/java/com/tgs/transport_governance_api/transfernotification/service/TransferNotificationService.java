package com.tgs.transport_governance_api.transfernotification.service;

import com.tgs.transport_governance_api.auth.service.CurrentUserService;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.transfernotification.dto.CreateTransferNotificationRequest;
import com.tgs.transport_governance_api.transfernotification.dto.TransferEventResponse;
import com.tgs.transport_governance_api.transfernotification.dto.TransferNotificationResponse;
import com.tgs.transport_governance_api.transfernotification.dto.UpdateDuesRequest;
import com.tgs.transport_governance_api.transfernotification.entity.TransferEvent;
import com.tgs.transport_governance_api.transfernotification.entity.TransferNotification;
import com.tgs.transport_governance_api.transfernotification.repository.TransferEventRepository;
import com.tgs.transport_governance_api.transfernotification.repository.TransferNotificationRepository;
import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.vendors.entity.Vendor;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferNotificationService {

    @Autowired
    private TransferNotificationRepository notificationRepository;

    @Autowired
    private TransferEventRepository eventRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentTransportRepository studentTransportRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private VendorRepository vendorRepository;

    // =====================================================
    // CREATE NOTIFICATION
    // =====================================================

    public TransferNotificationResponse createNotification(
            CreateTransferNotificationRequest request) {

        studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        if (notificationRepository
                .existsByStudentIdAndCompletedAtIsNull(
                        request.getStudentId())) {

            throw new RuntimeException(
                    "Active transfer notification already exists");
        }

        StudentTransport transport =
                studentTransportRepository
                        .findByStudentIdAndStatus(
                                request.getStudentId(),
                                "ACTIVE")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student transport assignment not found"));

        User currentUser =
                currentUserService.getCurrentUser();

        TransferNotification notification =
                new TransferNotification();

        notification.setStudentId(
                request.getStudentId());

        notification.setVendorId(
                transport.getVendorId());

        notification.setDuesStatus(
                "UNKNOWN");

        notification.setDuesAmount(
                BigDecimal.ZERO);

        notification.setCreatedBy(
                currentUser.getId());

        notification.setCreatedAt(
                LocalDateTime.now());

        notification.setVendorNotifiedAt(
                LocalDateTime.now());

        notification.setParentNotifiedAt(
                LocalDateTime.now());

        TransferNotification saved =
                notificationRepository.save(notification);

        createEvent(
                saved.getId(),
                "NOTIFICATION_CREATED",
                "Transfer notification created.",
                currentUser.getId());

        return mapToResponse(saved);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<TransferNotificationResponse>
    getAllNotifications() {

        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public TransferNotificationResponse
    getNotificationById(Integer id) {

        TransferNotification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"));

        return mapToResponse(notification);
    }

    public TransferNotificationResponse updateDues(
            Integer id,
            UpdateDuesRequest request) {

        TransferNotification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"));

        if (notification.getCompletedAt() != null) {
            throw new RuntimeException(
                    "Cannot update dues for a completed notification.");
        }
        User currentUser =
                currentUserService.getCurrentUser();

        Vendor vendor = vendorRepository
                .findByOwnerUserId(currentUser.getId())
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        if (!notification.getVendorId().equals(vendor.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this notification.");
        }

        notification.setDuesStatus(
                request.getDuesStatus());

        notification.setDuesAmount(
                request.getDuesAmount());

        TransferNotification updated =
                notificationRepository.save(notification);

        createEvent(
                updated.getId(),
                "DUES_UPDATED",
                request.getNotes(),
                currentUser.getId());

        return mapToResponse(updated);
    }

    public void completeNotification(
            Integer id) {

        TransferNotification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"));

        if (notification.getCompletedAt() != null) {
            throw new RuntimeException(
                    "Transfer notification is already completed.");
        }
        User currentUser =
                currentUserService.getCurrentUser();

        notification.setCompletedAt(
                LocalDateTime.now());

        notificationRepository.save(notification);

        createEvent(
                notification.getId(),
                "NOTIFICATION_COMPLETED",
                "Transfer notification completed.",
                currentUser.getId());
    }

    public List<TransferEventResponse> getNotificationEvents(
            Integer notificationId) {

        return eventRepository
                .findByNotificationIdOrderByCreatedAtAsc(
                        notificationId)
                .stream()
                .map(event -> {

                    TransferEventResponse response =
                            new TransferEventResponse();

                    response.setId(event.getId());
                    response.setEventType(event.getEventType());
                    response.setNotes(event.getNotes());
                    response.setPerformedBy(
                            event.getPerformedBy());
                    response.setCreatedAt(
                            event.getCreatedAt());

                    return response;

                }).toList();
    }

    private void createEvent(
            Integer notificationId,
            String eventType,
            String notes,
            Integer performedBy) {

        TransferEvent event = new TransferEvent();

        event.setNotificationId(notificationId);
        event.setEventType(eventType);
        event.setNotes(notes);
        event.setPerformedBy(performedBy);
        event.setCreatedAt(LocalDateTime.now());

        eventRepository.save(event);
    }

    private TransferNotificationResponse mapToResponse(
            TransferNotification notification) {

        TransferNotificationResponse response =
                new TransferNotificationResponse();

        response.setId(notification.getId());
        response.setStudentId(notification.getStudentId());
        response.setVendorId(notification.getVendorId());
        response.setDuesStatus(notification.getDuesStatus());
        response.setDuesAmount(notification.getDuesAmount());
        response.setCreatedBy(notification.getCreatedBy());
        response.setCreatedAt(notification.getCreatedAt());
        response.setVendorNotifiedAt(notification.getVendorNotifiedAt());
        response.setParentNotifiedAt(notification.getParentNotifiedAt());
        response.setCompletedAt(notification.getCompletedAt());

        return response;
    }
}
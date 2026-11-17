package com.tgs.transport_governance_api.transfernotification.service;

import com.tgs.transport_governance_api.auth.service.CurrentUserService;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.students.entity.Student;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferNotificationServiceTest {

    @Mock
    private TransferNotificationRepository notificationRepository;

    @Mock
    private TransferEventRepository eventRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentTransportRepository studentTransportRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private TransferNotificationService transferNotificationService;

    @Test
    @DisplayName("Should create transfer notification successfully and log created event")
    void createNotification_success() {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(1);

        Student student = new Student();
        student.setId(1);

        StudentTransport transport = new StudentTransport();
        transport.setId(5);
        transport.setStudentId(1);
        transport.setVendorId(20);
        transport.setStatus("ACTIVE");

        User user = new User();
        user.setId(99);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(notificationRepository.existsByStudentIdAndCompletedAtIsNull(1)).thenReturn(false);
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.of(transport));
        when(currentUserService.getCurrentUser()).thenReturn(user);

        when(notificationRepository.save(any(TransferNotification.class))).thenAnswer(invocation -> {
            TransferNotification n = invocation.getArgument(0);
            n.setId(101);
            return n;
        });

        TransferNotificationResponse response = transferNotificationService.createNotification(request);

        assertNotNull(response);
        assertEquals(101, response.getId());
        assertEquals(1, response.getStudentId());
        assertEquals(20, response.getVendorId());
        assertEquals("UNKNOWN", response.getDuesStatus());
        assertEquals(BigDecimal.ZERO, response.getDuesAmount());
        assertEquals(99, response.getCreatedBy());

        ArgumentCaptor<TransferEvent> eventCaptor = ArgumentCaptor.forClass(TransferEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        TransferEvent event = eventCaptor.getValue();
        assertEquals(101, event.getNotificationId());
        assertEquals("NOTIFICATION_CREATED", event.getEventType());
        assertEquals(99, event.getPerformedBy());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student does not exist")
    void createNotification_studentNotFound() {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(999);

        when(studentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.createNotification(request));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when an active notification already exists")
    void createNotification_activeNotificationAlreadyExists() {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(1);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(notificationRepository.existsByStudentIdAndCompletedAtIsNull(1)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> transferNotificationService.createNotification(request));
        verify(studentTransportRepository, never()).findByStudentIdAndStatus(any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when active student transport assignment not found")
    void createNotification_transportNotFound() {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(1);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(notificationRepository.existsByStudentIdAndCompletedAtIsNull(1)).thenReturn(false);
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.createNotification(request));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all transfer notifications")
    void getAllNotifications_success() {
        TransferNotification n = new TransferNotification();
        n.setId(1);
        n.setStudentId(10);
        n.setVendorId(20);

        when(notificationRepository.findAll()).thenReturn(List.of(n));

        List<TransferNotificationResponse> result = transferNotificationService.getAllNotifications();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    @DisplayName("Should get notification by ID successfully")
    void getNotificationById_success() {
        TransferNotification n = new TransferNotification();
        n.setId(1);
        n.setStudentId(10);
        n.setVendorId(20);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(n));

        TransferNotificationResponse result = transferNotificationService.getNotificationById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when notification ID does not exist")
    void getNotificationById_notFound() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.getNotificationById(999));
    }

    @Test
    @DisplayName("Should update dues successfully when authorized vendor submits update")
    void updateDues_success() {
        UpdateDuesRequest request = new UpdateDuesRequest();
        request.setDuesStatus("CLEAR");
        request.setDuesAmount(BigDecimal.ZERO);
        request.setNotes("All fees cleared");

        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setVendorId(10);
        notification.setCompletedAt(null);

        User currentUser = new User();
        currentUser.setId(5);

        Vendor vendor = new Vendor();
        vendor.setId(10);
        vendor.setOwnerUserId(5);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(vendorRepository.findByOwnerUserId(5)).thenReturn(Optional.of(vendor));
        when(notificationRepository.save(any(TransferNotification.class))).thenAnswer(i -> i.getArgument(0));

        TransferNotificationResponse response = transferNotificationService.updateDues(1, request);

        assertEquals("CLEAR", response.getDuesStatus());
        assertEquals(BigDecimal.ZERO, response.getDuesAmount());

        ArgumentCaptor<TransferEvent> eventCaptor = ArgumentCaptor.forClass(TransferEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertEquals("DUES_UPDATED", eventCaptor.getValue().getEventType());
        assertEquals("All fees cleared", eventCaptor.getValue().getNotes());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating dues for non-existing notification")
    void updateDues_notFound() {
        UpdateDuesRequest request = new UpdateDuesRequest();
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.updateDues(999, request));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating dues for an already completed notification")
    void updateDues_alreadyCompleted() {
        UpdateDuesRequest request = new UpdateDuesRequest();
        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setCompletedAt(LocalDateTime.now());

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

        assertThrows(BadRequestException.class, () -> transferNotificationService.updateDues(1, request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vendor for current user is not found")
    void updateDues_vendorNotFound() {
        UpdateDuesRequest request = new UpdateDuesRequest();
        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setCompletedAt(null);

        User currentUser = new User();
        currentUser.setId(5);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(vendorRepository.findByOwnerUserId(5)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.updateDues(1, request));
    }

    @Test
    @DisplayName("Should throw RuntimeException when current vendor is not authorized for this notification")
    void updateDues_unauthorizedVendor() {
        UpdateDuesRequest request = new UpdateDuesRequest();
        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setVendorId(10);
        notification.setCompletedAt(null);

        User currentUser = new User();
        currentUser.setId(5);

        Vendor otherVendor = new Vendor();
        otherVendor.setId(99); // does not match 10

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(vendorRepository.findByOwnerUserId(5)).thenReturn(Optional.of(otherVendor));

        assertThrows(RuntimeException.class, () -> transferNotificationService.updateDues(1, request));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should complete notification successfully and log completed event")
    void completeNotification_success() {
        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setCompletedAt(null);

        User currentUser = new User();
        currentUser.setId(8);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        transferNotificationService.completeNotification(1);

        assertNotNull(notification.getCompletedAt());
        verify(notificationRepository).save(notification);

        ArgumentCaptor<TransferEvent> eventCaptor = ArgumentCaptor.forClass(TransferEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertEquals("NOTIFICATION_COMPLETED", eventCaptor.getValue().getEventType());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when completing non-existent notification")
    void completeNotification_notFound() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transferNotificationService.completeNotification(999));
    }

    @Test
    @DisplayName("Should throw BadRequestException when completing already completed notification")
    void completeNotification_alreadyCompleted() {
        TransferNotification notification = new TransferNotification();
        notification.setId(1);
        notification.setCompletedAt(LocalDateTime.now());

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

        assertThrows(BadRequestException.class, () -> transferNotificationService.completeNotification(1));
    }

    @Test
    @DisplayName("Should return event trail ordered by created timestamp")
    void getNotificationEvents_success() {
        TransferEvent event = new TransferEvent();
        event.setId(1);
        event.setNotificationId(10);
        event.setEventType("NOTIFICATION_CREATED");
        event.setNotes("Created");
        event.setPerformedBy(2);
        event.setCreatedAt(LocalDateTime.now());

        when(eventRepository.findByNotificationIdOrderByCreatedAtAsc(10)).thenReturn(List.of(event));

        List<TransferEventResponse> events = transferNotificationService.getNotificationEvents(10);

        assertEquals(1, events.size());
        assertEquals("NOTIFICATION_CREATED", events.get(0).getEventType());
        assertEquals("Created", events.get(0).getNotes());
    }
}


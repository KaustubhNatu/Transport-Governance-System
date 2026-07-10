package com.tgs.transport_governance_api.transfernotification.controller;

import com.tgs.transport_governance_api.transfernotification.dto.CreateTransferNotificationRequest;
import com.tgs.transport_governance_api.transfernotification.dto.TransferEventResponse;
import com.tgs.transport_governance_api.transfernotification.dto.TransferNotificationResponse;
import com.tgs.transport_governance_api.transfernotification.dto.UpdateDuesRequest;
import com.tgs.transport_governance_api.transfernotification.service.TransferNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer-notifications")
public class TransferNotificationController {

    @Autowired
    private TransferNotificationService transferNotificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferNotificationResponse createNotification(
            @RequestBody CreateTransferNotificationRequest request) {

        return transferNotificationService.createNotification(request);
    }

    @GetMapping
    public List<TransferNotificationResponse> getAllNotifications() {

        return transferNotificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public TransferNotificationResponse getNotificationById(
            @PathVariable Integer id) {

        return transferNotificationService.getNotificationById(id);
    }

    @PutMapping("/{id}/dues")
    public TransferNotificationResponse updateDues(
            @PathVariable Integer id,
            @RequestBody UpdateDuesRequest request) {

        return transferNotificationService.updateDues(id, request);
    }

    @PutMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeNotification(
            @PathVariable Integer id) {

        transferNotificationService.completeNotification(id);
    }

    @GetMapping("/{id}/events")
    public List<TransferEventResponse> getNotificationEvents(
            @PathVariable Integer id) {

        return transferNotificationService
                .getNotificationEvents(id);
    }
}
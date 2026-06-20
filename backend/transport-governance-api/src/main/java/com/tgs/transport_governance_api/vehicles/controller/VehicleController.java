package com.tgs.transport_governance_api.vehicles.controller;

import com.tgs.transport_governance_api.vehicles.dto.CreateVehicleRequest;
import com.tgs.transport_governance_api.vehicles.dto.VehicleResponse;
import com.tgs.transport_governance_api.vehicles.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public VehicleResponse createVehicle(
            @RequestBody @Valid
            CreateVehicleRequest request) {

        return vehicleService.createVehicle(request);
    }

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {

        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public VehicleResponse getVehicleById(
            @PathVariable Integer id) {

        return vehicleService.getVehicleById(id);
    }
}
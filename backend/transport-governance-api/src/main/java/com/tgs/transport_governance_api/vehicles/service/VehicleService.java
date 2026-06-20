package com.tgs.transport_governance_api.vehicles.service;

import com.tgs.transport_governance_api.vehicles.dto.CreateVehicleRequest;
import com.tgs.transport_governance_api.vehicles.dto.VehicleResponse;
import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public VehicleResponse createVehicle(CreateVehicleRequest request) {

        if (vehicleRepository
                .findByRegistrationNumber(
                        request.getRegistrationNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Vehicle already exists");
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setVendorId(request.getVendorId());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setManufactureYear(request.getManufactureYear());
        vehicle.setCreatedAt(LocalDateTime.now());

        Vehicle savedVehicle =
                vehicleRepository.save(vehicle);

        return mapToResponse(savedVehicle);
    }

    public List<VehicleResponse> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public VehicleResponse getVehicleById(Integer id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found"));

        return mapToResponse(vehicle);
    }

    private VehicleResponse mapToResponse(
            Vehicle vehicle) {

        VehicleResponse response =
                new VehicleResponse();

        response.setId(vehicle.getId());
        response.setVendorId(vehicle.getVendorId());
        response.setRegistrationNumber(
                vehicle.getRegistrationNumber());
        response.setCapacity(vehicle.getCapacity());
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setManufactureYear(
                vehicle.getManufactureYear());
        response.setCreatedAt(vehicle.getCreatedAt());

        return response;
    }
}
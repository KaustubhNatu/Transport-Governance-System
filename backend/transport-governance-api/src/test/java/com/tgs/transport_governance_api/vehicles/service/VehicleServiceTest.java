package com.tgs.transport_governance_api.vehicles.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.vehicles.dto.CreateVehicleRequest;
import com.tgs.transport_governance_api.vehicles.dto.VehicleResponse;
import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    @DisplayName("createVehicle: successfully creates vehicle when registration number is unique")
    void createVehicle_success() {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setVendorId(1);
        request.setRegistrationNumber("KA-01-AB-1234");
        request.setCapacity(30);
        request.setMake("Tata");
        request.setModel("Starbus");
        request.setManufactureYear(2022);

        when(vehicleRepository.findByRegistrationNumber("KA-01-AB-1234")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle v = invocation.getArgument(0);
            v.setId(5);
            return v;
        });

        VehicleResponse response = vehicleService.createVehicle(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5);
        assertThat(response.getRegistrationNumber()).isEqualTo("KA-01-AB-1234");
        assertThat(response.getCapacity()).isEqualTo(30);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("createVehicle: throws BadRequestException when registration number already exists")
    void createVehicle_throwsBadRequestException_whenDuplicate() {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setRegistrationNumber("KA-01-AB-1234");

        when(vehicleRepository.findByRegistrationNumber("KA-01-AB-1234"))
                .thenReturn(Optional.of(new Vehicle()));

        assertThrows(BadRequestException.class, () -> vehicleService.createVehicle(request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("getVehicleById: returns vehicle when found")
    void getVehicleById_success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(5);
        vehicle.setRegistrationNumber("KA-01-AB-1234");

        when(vehicleRepository.findById(5)).thenReturn(Optional.of(vehicle));

        VehicleResponse response = vehicleService.getVehicleById(5);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5);
    }

    @Test
    @DisplayName("getVehicleById: throws ResourceNotFoundException when not found")
    void getVehicleById_notFound() {
        when(vehicleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById(99));
    }

    @Test
    @DisplayName("getAllVehicles: returns list of all vehicles")
    void getAllVehicles_success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<VehicleResponse> list = vehicleService.getAllVehicles();

        assertThat(list).hasSize(1);
    }
}


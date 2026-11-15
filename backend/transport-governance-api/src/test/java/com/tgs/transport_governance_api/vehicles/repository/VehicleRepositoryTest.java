package com.tgs.transport_governance_api.vehicles.repository;

import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByRegistrationNumber: finds vehicle by registration number")
    void findByRegistrationNumber_found() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVendorId(1);
        vehicle.setRegistrationNumber("MH-02-CD-5678");
        vehicle.setCapacity(25);
        vehicle.setMake("Force");
        vehicle.setModel("Traveller");
        vehicle.setManufactureYear(2021);
        vehicle.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByRegistrationNumber("MH-02-CD-5678");

        assertThat(found).isPresent();
        assertThat(found.get().getRegistrationNumber()).isEqualTo("MH-02-CD-5678");
    }

    @Test
    @DisplayName("findByRegistrationNumber: returns empty when registration number not found")
    void findByRegistrationNumber_notFound() {
        Optional<Vehicle> found = vehicleRepository.findByRegistrationNumber("UNKNOWN-REG");
        assertThat(found).isEmpty();
    }
}


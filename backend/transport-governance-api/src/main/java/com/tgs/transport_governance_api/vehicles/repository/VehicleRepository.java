package com.tgs.transport_governance_api.vehicles.repository;

import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Integer> {

    Optional<Vehicle> findByRegistrationNumber(
            String registrationNumber);

}
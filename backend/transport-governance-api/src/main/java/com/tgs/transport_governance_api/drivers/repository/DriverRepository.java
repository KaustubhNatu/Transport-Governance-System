package com.tgs.transport_governance_api.drivers.repository;

import com.tgs.transport_governance_api.drivers.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository
        extends JpaRepository<Driver, Integer> {

    Optional<Driver> findByLicenseNumber(
            String licenseNumber);

}
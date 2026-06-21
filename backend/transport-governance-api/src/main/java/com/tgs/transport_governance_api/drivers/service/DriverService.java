package com.tgs.transport_governance_api.drivers.service;

import com.tgs.transport_governance_api.drivers.dto.CreateDriverRequest;
import com.tgs.transport_governance_api.drivers.dto.DriverResponse;
import com.tgs.transport_governance_api.drivers.entity.Driver;
import com.tgs.transport_governance_api.drivers.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public DriverResponse createDriver(
            CreateDriverRequest request) {

        if (driverRepository
                .findByLicenseNumber(
                        request.getLicenseNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Driver already exists");
        }

        Driver driver = new Driver();

        driver.setUserId(request.getUserId());
        driver.setVendorId(request.getVendorId());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setBackgroundVerified(
                request.getBackgroundVerified());

        driver.setCreatedAt(LocalDateTime.now());

        Driver savedDriver =
                driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    public List<DriverResponse> getAllDrivers() {

        return driverRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DriverResponse getDriverById(Integer id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Driver not found"));

        return mapToResponse(driver);
    }

    private DriverResponse mapToResponse(
            Driver driver) {

        DriverResponse response =
                new DriverResponse();

        response.setId(driver.getId());
        response.setUserId(driver.getUserId());
        response.setVendorId(driver.getVendorId());
        response.setLicenseNumber(
                driver.getLicenseNumber());
        response.setLicenseExpiry(
                driver.getLicenseExpiry());
        response.setBackgroundVerified(
                driver.getBackgroundVerified());
        response.setCreatedAt(
                driver.getCreatedAt());

        return response;
    }
}
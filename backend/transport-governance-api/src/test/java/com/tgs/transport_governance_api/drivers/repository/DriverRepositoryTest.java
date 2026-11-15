package com.tgs.transport_governance_api.drivers.repository;

import com.tgs.transport_governance_api.drivers.entity.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByLicenseNumber: finds driver by unique license number")
    void findByLicenseNumber_found() {
        Driver driver = new Driver();
        driver.setUserId(1);
        driver.setVendorId(2);
        driver.setLicenseNumber("DL-STATE-999");
        driver.setLicenseExpiry(LocalDate.now().plusYears(1));
        driver.setBackgroundVerified(true);
        driver.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(driver);

        Optional<Driver> found = driverRepository.findByLicenseNumber("DL-STATE-999");

        assertThat(found).isPresent();
        assertThat(found.get().getLicenseNumber()).isEqualTo("DL-STATE-999");
    }

    @Test
    @DisplayName("findByLicenseNumber: returns empty when license not found")
    void findByLicenseNumber_notFound() {
        Optional<Driver> found = driverRepository.findByLicenseNumber("UNKNOWN-DL");
        assertThat(found).isEmpty();
    }
}


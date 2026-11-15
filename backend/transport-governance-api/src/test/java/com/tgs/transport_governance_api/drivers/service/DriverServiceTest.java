package com.tgs.transport_governance_api.drivers.service;

import com.tgs.transport_governance_api.drivers.dto.CreateDriverRequest;
import com.tgs.transport_governance_api.drivers.dto.DriverResponse;
import com.tgs.transport_governance_api.drivers.entity.Driver;
import com.tgs.transport_governance_api.drivers.repository.DriverRepository;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    @DisplayName("createDriver: successfully creates driver when license is unique")
    void createDriver_success() {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setUserId(10);
        request.setVendorId(2);
        request.setLicenseNumber("DL-12345");
        request.setLicenseExpiry(LocalDate.now().plusYears(2));
        request.setBackgroundVerified(true);

        when(driverRepository.findByLicenseNumber("DL-12345")).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver d = invocation.getArgument(0);
            d.setId(1);
            return d;
        });

        DriverResponse response = driverService.createDriver(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getLicenseNumber()).isEqualTo("DL-12345");
        assertThat(response.getBackgroundVerified()).isTrue();
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    @DisplayName("createDriver: throws RuntimeException when license number already exists")
    void createDriver_throwsRuntimeException_whenDuplicate() {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setLicenseNumber("DL-12345");

        when(driverRepository.findByLicenseNumber("DL-12345")).thenReturn(Optional.of(new Driver()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.createDriver(request));
        assertThat(exception.getMessage()).isEqualTo("Driver already exists");
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("getDriverById: returns driver when found")
    void getDriverById_success() {
        Driver driver = new Driver();
        driver.setId(1);
        driver.setLicenseNumber("DL-999");

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        DriverResponse response = driverService.getDriverById(1);

        assertThat(response).isNotNull();
        assertThat(response.getLicenseNumber()).isEqualTo("DL-999");
    }

    @Test
    @DisplayName("getDriverById: throws ResourceNotFoundException when not found")
    void getDriverById_notFound() {
        when(driverRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.getDriverById(99));
    }

    @Test
    @DisplayName("getAllDrivers: returns all drivers")
    void getAllDrivers_success() {
        Driver driver = new Driver();
        driver.setId(1);

        when(driverRepository.findAll()).thenReturn(List.of(driver));

        List<DriverResponse> list = driverService.getAllDrivers();

        assertThat(list).hasSize(1);
    }
}


package com.tgs.transport_governance_api.drivers.controller;

import com.tgs.transport_governance_api.drivers.dto.CreateDriverRequest;
import com.tgs.transport_governance_api.drivers.dto.DriverResponse;
import com.tgs.transport_governance_api.drivers.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    public DriverResponse createDriver(
            @RequestBody @Valid
            CreateDriverRequest request) {

        return driverService.createDriver(request);
    }

    @GetMapping
    public List<DriverResponse> getAllDrivers() {

        return driverService.getAllDrivers();
    }

    @GetMapping("/{id}")
    public DriverResponse getDriverById(
            @PathVariable Integer id) {

        return driverService.getDriverById(id);
    }
}
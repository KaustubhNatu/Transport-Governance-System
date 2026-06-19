package com.tgs.transport_governance_api.vendors.controller;

import com.tgs.transport_governance_api.vendors.dto.CreateVendorRequest;
import com.tgs.transport_governance_api.vendors.dto.VendorResponse;
import com.tgs.transport_governance_api.vendors.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public VendorResponse createVendor(
            @RequestBody @Valid CreateVendorRequest request) {

        return vendorService.createVendor(request);
    }

    @GetMapping
    public List<VendorResponse> getAllVendors() {

        return vendorService.getAllVendors();
    }

    @GetMapping("/{id}")
    public VendorResponse getVendorById(
            @PathVariable Integer id) {

        return vendorService.getVendorById(id);
    }
}
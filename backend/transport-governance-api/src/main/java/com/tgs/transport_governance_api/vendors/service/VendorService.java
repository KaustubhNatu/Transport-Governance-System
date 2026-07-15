package com.tgs.transport_governance_api.vendors.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.vendors.dto.CreateVendorRequest;
import com.tgs.transport_governance_api.vendors.dto.VendorResponse;
import com.tgs.transport_governance_api.vendors.entity.Vendor;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public VendorResponse createVendor(CreateVendorRequest request) {

        if (vendorRepository
                .findBySchoolIdAndCompanyName(
                        request.getSchoolId(),
                        request.getCompanyName())
                .isPresent()) {

            throw new BadRequestException(
                    "Vendor already exists");
        }

        Vendor vendor = new Vendor();

        vendor.setSchoolId(request.getSchoolId());
        vendor.setOwnerUserId(request.getOwnerUserId());
        vendor.setCompanyName(request.getCompanyName());
        vendor.setContactPhone(request.getContactPhone());
        vendor.setCreatedAt(LocalDateTime.now());

        Vendor savedVendor = vendorRepository.save(vendor);

        return mapToResponse(savedVendor);
    }

    public List<VendorResponse> getAllVendors() {

        return vendorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public VendorResponse getVendorById(Integer id) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor not found"));

        return mapToResponse(vendor);
    }

    private VendorResponse mapToResponse(Vendor vendor) {

        VendorResponse response = new VendorResponse();

        response.setId(vendor.getId());
        response.setSchoolId(vendor.getSchoolId());
        response.setOwnerUserId(vendor.getOwnerUserId());
        response.setCompanyName(vendor.getCompanyName());
        response.setContactPhone(vendor.getContactPhone());
        response.setCreatedAt(vendor.getCreatedAt());

        return response;
    }
}
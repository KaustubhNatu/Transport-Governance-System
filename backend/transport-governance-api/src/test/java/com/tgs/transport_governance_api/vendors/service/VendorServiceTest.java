package com.tgs.transport_governance_api.vendors.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.vendors.dto.CreateVendorRequest;
import com.tgs.transport_governance_api.vendors.dto.VendorResponse;
import com.tgs.transport_governance_api.vendors.entity.Vendor;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
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
class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorService vendorService;

    @Test
    @DisplayName("createVendor: creates vendor successfully when unique for school")
    void createVendor_success() {
        CreateVendorRequest request = new CreateVendorRequest();
        request.setSchoolId(1);
        request.setOwnerUserId(5);
        request.setCompanyName("Express Transport");
        request.setContactPhone("555-8888");

        when(vendorRepository.findBySchoolIdAndCompanyName(1, "Express Transport")).thenReturn(Optional.empty());
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(invocation -> {
            Vendor v = invocation.getArgument(0);
            v.setId(10);
            return v;
        });

        VendorResponse response = vendorService.createVendor(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getCompanyName()).isEqualTo("Express Transport");
        verify(vendorRepository).save(any(Vendor.class));
    }

    @Test
    @DisplayName("createVendor: throws BadRequestException when company name duplicate for school")
    void createVendor_throwsBadRequestException_whenDuplicate() {
        CreateVendorRequest request = new CreateVendorRequest();
        request.setSchoolId(1);
        request.setCompanyName("Express Transport");

        when(vendorRepository.findBySchoolIdAndCompanyName(1, "Express Transport"))
                .thenReturn(Optional.of(new Vendor()));

        assertThrows(BadRequestException.class, () -> vendorService.createVendor(request));
        verify(vendorRepository, never()).save(any());
    }

    @Test
    @DisplayName("getVendorById: returns vendor when found")
    void getVendorById_success() {
        Vendor vendor = new Vendor();
        vendor.setId(10);
        vendor.setCompanyName("Express Transport");

        when(vendorRepository.findById(10)).thenReturn(Optional.of(vendor));

        VendorResponse response = vendorService.getVendorById(10);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
    }

    @Test
    @DisplayName("getVendorById: throws ResourceNotFoundException when not found")
    void getVendorById_notFound() {
        when(vendorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vendorService.getVendorById(99));
    }

    @Test
    @DisplayName("getAllVendors: returns list of mapped vendors")
    void getAllVendors_success() {
        Vendor vendor = new Vendor();
        vendor.setId(1);
        vendor.setCompanyName("Vendor 1");

        when(vendorRepository.findAll()).thenReturn(List.of(vendor));

        List<VendorResponse> list = vendorService.getAllVendors();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getCompanyName()).isEqualTo("Vendor 1");
    }
}


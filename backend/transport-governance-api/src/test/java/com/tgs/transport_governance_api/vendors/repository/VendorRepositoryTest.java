package com.tgs.transport_governance_api.vendors.repository;

import com.tgs.transport_governance_api.vendors.entity.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendorRepositoryTest {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findBySchoolIdAndCompanyName: finds vendor by school ID and company name")
    void findBySchoolIdAndCompanyName_found() {
        Vendor vendor = new Vendor();
        vendor.setSchoolId(1);
        vendor.setOwnerUserId(10);
        vendor.setCompanyName("City Transit");
        vendor.setContactPhone("555-4321");
        vendor.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(vendor);

        Optional<Vendor> found = vendorRepository.findBySchoolIdAndCompanyName(1, "City Transit");

        assertThat(found).isPresent();
        assertThat(found.get().getCompanyName()).isEqualTo("City Transit");
    }

    @Test
    @DisplayName("findByOwnerUserId: finds vendor by owner user ID")
    void findByOwnerUserId_found() {
        Vendor vendor = new Vendor();
        vendor.setSchoolId(2);
        vendor.setOwnerUserId(25);
        vendor.setCompanyName("Metro Bus");
        vendor.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(vendor);

        Optional<Vendor> found = vendorRepository.findByOwnerUserId(25);

        assertThat(found).isPresent();
        assertThat(found.get().getOwnerUserId()).isEqualTo(25);
    }
}


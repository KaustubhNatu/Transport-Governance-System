package com.tgs.transport_governance_api.vendors.repository;

import com.tgs.transport_governance_api.vendors.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findBySchoolIdAndCompanyName(
            Integer schoolId,
            String companyName);
}
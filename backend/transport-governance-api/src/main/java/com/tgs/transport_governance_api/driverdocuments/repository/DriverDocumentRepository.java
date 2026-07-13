package com.tgs.transport_governance_api.driverdocuments.repository;

import com.tgs.transport_governance_api.driverdocuments.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDocumentRepository
        extends JpaRepository<DriverDocument, Integer> {

    List<DriverDocument> findByDriverId(Integer driverId);

}
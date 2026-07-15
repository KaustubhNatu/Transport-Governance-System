package com.tgs.transport_governance_api.vehicledocuments.repository;

import com.tgs.transport_governance_api.vehicledocuments.entity.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleDocumentRepository
        extends JpaRepository<VehicleDocument, Integer> {

    List<VehicleDocument> findByVehicleId(Integer vehicleId);

    boolean existsByVehicleIdAndDocumentType(
            Integer vehicleId,
            String documentType);

}
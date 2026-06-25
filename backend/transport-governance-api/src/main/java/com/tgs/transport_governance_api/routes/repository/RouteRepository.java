package com.tgs.transport_governance_api.routes.repository;

import com.tgs.transport_governance_api.routes.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository
        extends JpaRepository<Route, Integer> {

    Optional<Route> findByVendorIdAndRouteName(
            Integer vendorId,
            String routeName);

}
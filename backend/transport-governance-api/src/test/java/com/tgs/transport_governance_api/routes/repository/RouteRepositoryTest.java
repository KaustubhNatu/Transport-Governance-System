package com.tgs.transport_governance_api.routes.repository;

import com.tgs.transport_governance_api.routes.entity.Route;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RouteRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByVendorIdAndRouteName: finds route by vendor and route name")
    void findByVendorIdAndRouteName_found() {
        Route route = new Route();
        route.setVendorId(1);
        route.setRouteName("Express Line 1");
        route.setStartLocation("City Center");
        route.setEndLocation("School Campus");
        route.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(route);

        Optional<Route> found = routeRepository.findByVendorIdAndRouteName(1, "Express Line 1");

        assertThat(found).isPresent();
        assertThat(found.get().getRouteName()).isEqualTo("Express Line 1");
    }

    @Test
    @DisplayName("findByVendorIdAndRouteName: returns empty when not found")
    void findByVendorIdAndRouteName_notFound() {
        Optional<Route> found = routeRepository.findByVendorIdAndRouteName(99, "Non-existent");
        assertThat(found).isEmpty();
    }
}


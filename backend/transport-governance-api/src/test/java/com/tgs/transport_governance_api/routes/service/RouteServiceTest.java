package com.tgs.transport_governance_api.routes.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.routes.dto.CreateRouteRequest;
import com.tgs.transport_governance_api.routes.dto.RouteResponse;
import com.tgs.transport_governance_api.routes.entity.Route;
import com.tgs.transport_governance_api.routes.repository.RouteRepository;
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
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    @Test
    @DisplayName("createRoute: successfully creates route when name is unique for vendor")
    void createRoute_success() {
        CreateRouteRequest request = new CreateRouteRequest();
        request.setVendorId(1);
        request.setRouteName("North Route");
        request.setStartLocation("Point A");
        request.setEndLocation("Point B");
        request.setVehicleId(2);
        request.setDriverId(3);

        when(routeRepository.findByVendorIdAndRouteName(1, "North Route")).thenReturn(Optional.empty());
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> {
            Route r = invocation.getArgument(0);
            r.setId(10);
            return r;
        });

        RouteResponse response = routeService.createRoute(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getRouteName()).isEqualTo("North Route");
        verify(routeRepository).save(any(Route.class));
    }

    @Test
    @DisplayName("createRoute: throws BadRequestException when route name duplicate for vendor")
    void createRoute_throwsBadRequestException_whenDuplicate() {
        CreateRouteRequest request = new CreateRouteRequest();
        request.setVendorId(1);
        request.setRouteName("North Route");

        when(routeRepository.findByVendorIdAndRouteName(1, "North Route"))
                .thenReturn(Optional.of(new Route()));

        assertThrows(BadRequestException.class, () -> routeService.createRoute(request));
        verify(routeRepository, never()).save(any());
    }

    @Test
    @DisplayName("getRouteById: returns route when found")
    void getRouteById_success() {
        Route route = new Route();
        route.setId(10);
        route.setRouteName("North Route");

        when(routeRepository.findById(10)).thenReturn(Optional.of(route));

        RouteResponse response = routeService.getRouteById(10);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
    }

    @Test
    @DisplayName("getRouteById: throws ResourceNotFoundException when not found")
    void getRouteById_notFound() {
        when(routeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> routeService.getRouteById(99));
    }

    @Test
    @DisplayName("getAllRoutes: returns list of routes")
    void getAllRoutes_success() {
        Route route = new Route();
        route.setId(1);

        when(routeRepository.findAll()).thenReturn(List.of(route));

        List<RouteResponse> list = routeService.getAllRoutes();

        assertThat(list).hasSize(1);
    }
}


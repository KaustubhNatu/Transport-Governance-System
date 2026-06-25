package com.tgs.transport_governance_api.routes.service;

import com.tgs.transport_governance_api.routes.dto.CreateRouteRequest;
import com.tgs.transport_governance_api.routes.dto.RouteResponse;
import com.tgs.transport_governance_api.routes.entity.Route;
import com.tgs.transport_governance_api.routes.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public RouteResponse createRoute(CreateRouteRequest request) {

        if (routeRepository
                .findByVendorIdAndRouteName(
                        request.getVendorId(),
                        request.getRouteName())
                .isPresent()) {

            throw new RuntimeException(
                    "Route already exists");
        }

        Route route = new Route();

        route.setVendorId(request.getVendorId());
        route.setRouteName(request.getRouteName());
        route.setStartLocation(request.getStartLocation());
        route.setEndLocation(request.getEndLocation());
        route.setVehicleId(request.getVehicleId());
        route.setDriverId(request.getDriverId());
        route.setCreatedAt(LocalDateTime.now());

        Route savedRoute =
                routeRepository.save(route);

        return mapToResponse(savedRoute);
    }

    public List<RouteResponse> getAllRoutes() {

        return routeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RouteResponse getRouteById(Integer id) {

        Route route = routeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Route not found"));

        return mapToResponse(route);
    }

    private RouteResponse mapToResponse(Route route) {

        RouteResponse response =
                new RouteResponse();

        response.setId(route.getId());
        response.setVendorId(route.getVendorId());
        response.setRouteName(route.getRouteName());
        response.setStartLocation(route.getStartLocation());
        response.setEndLocation(route.getEndLocation());
        response.setVehicleId(route.getVehicleId());
        response.setDriverId(route.getDriverId());
        response.setCreatedAt(route.getCreatedAt());

        return response;
    }
}
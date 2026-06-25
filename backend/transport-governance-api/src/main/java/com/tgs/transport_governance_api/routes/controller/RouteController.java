package com.tgs.transport_governance_api.routes.controller;

import com.tgs.transport_governance_api.routes.dto.CreateRouteRequest;
import com.tgs.transport_governance_api.routes.dto.RouteResponse;
import com.tgs.transport_governance_api.routes.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping
    public RouteResponse createRoute(
            @RequestBody @Valid
            CreateRouteRequest request) {

        return routeService.createRoute(request);
    }

    @GetMapping
    public List<RouteResponse> getAllRoutes() {

        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public RouteResponse getRouteById(
            @PathVariable Integer id) {

        return routeService.getRouteById(id);
    }
}
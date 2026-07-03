package com.tgs.transport_governance_api.studenttransport.service;

import com.tgs.transport_governance_api.routes.entity.Route;
import com.tgs.transport_governance_api.routes.repository.RouteRepository;
import com.tgs.transport_governance_api.studenttransport.dto.CreateStudentTransportRequest;
import com.tgs.transport_governance_api.studenttransport.dto.StudentTransportResponse;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentTransportService {

    @Autowired
    private StudentTransportRepository studentTransportRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public StudentTransportResponse createStudentTransport(
            CreateStudentTransportRequest request) {

        studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        vendorRepository.findById(request.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        if (studentTransportRepository
                .findByStudentIdAndStatus(
                        request.getStudentId(),
                        "ACTIVE")
                .isPresent()) {

            throw new RuntimeException(
                    "Student already has an active transport assignment");
        }

        if (request.getRouteId() != null) {

            Route route = routeRepository.findById(
                            request.getRouteId())
                    .orElseThrow(() ->
                            new RuntimeException("Route not found"));

            if (!route.getVendorId().equals(
                    request.getVendorId())) {

                throw new RuntimeException(
                        "Route does not belong to vendor");
            }
        }

        if (request.getVehicleId() != null) {

            Vehicle vehicle = vehicleRepository.findById(
                            request.getVehicleId())
                    .orElseThrow(() ->
                            new RuntimeException("Vehicle not found"));

            if (!vehicle.getVendorId().equals(
                    request.getVendorId())) {

                throw new RuntimeException(
                        "Vehicle does not belong to vendor");
            }
        }

        if (request.getRouteId() != null &&
                request.getVehicleId() != null) {

            Route route = routeRepository.findById(
                            request.getRouteId())
                    .orElseThrow(() ->
                            new RuntimeException("Route not found"));

            if (route.getVehicleId() == null) {
                throw new RuntimeException(
                        "No vehicle assigned to route");
            }

            if (!route.getVehicleId().equals(
                    request.getVehicleId())) {

                throw new RuntimeException(
                        "Vehicle does not match selected route");
            }

            if (route.getDriverId() == null) {
                throw new RuntimeException(
                        "No driver assigned to route");
            }
        }

        StudentTransport transport =
                new StudentTransport();

        transport.setStudentId(request.getStudentId());
        transport.setVendorId(request.getVendorId());
        transport.setRouteId(request.getRouteId());
        transport.setVehicleId(request.getVehicleId());

        transport.setEnrollmentDate(LocalDate.now());
        transport.setStatus("ACTIVE");
        transport.setCreatedAt(LocalDateTime.now());

        StudentTransport saved =
                studentTransportRepository.save(transport);

        return mapToResponse(saved);
    }

    public List<StudentTransportResponse> getAllStudentTransport() {

        return studentTransportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StudentTransportResponse getStudentTransportById(
            Integer id) {

        StudentTransport transport =
                studentTransportRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assignment not found"));

        return mapToResponse(transport);
    }

    private StudentTransportResponse mapToResponse(
            StudentTransport transport) {

        StudentTransportResponse response =
                new StudentTransportResponse();

        response.setId(transport.getId());
        response.setStudentId(transport.getStudentId());
        response.setVendorId(transport.getVendorId());
        response.setRouteId(transport.getRouteId());
        response.setVehicleId(transport.getVehicleId());
        response.setEnrollmentDate(
                transport.getEnrollmentDate());
        response.setStatus(transport.getStatus());
        response.setCreatedAt(
                transport.getCreatedAt());

        return response;
    }
}
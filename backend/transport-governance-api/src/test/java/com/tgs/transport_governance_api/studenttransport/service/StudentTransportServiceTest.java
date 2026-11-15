package com.tgs.transport_governance_api.studenttransport.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.routes.entity.Route;
import com.tgs.transport_governance_api.routes.repository.RouteRepository;
import com.tgs.transport_governance_api.students.entity.Student;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.studenttransport.dto.CreateStudentTransportRequest;
import com.tgs.transport_governance_api.studenttransport.dto.StudentTransportResponse;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
import com.tgs.transport_governance_api.vehicles.entity.Vehicle;
import com.tgs.transport_governance_api.vehicles.repository.VehicleRepository;
import com.tgs.transport_governance_api.vendors.entity.Vendor;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentTransportServiceTest {

    @Mock
    private StudentTransportRepository studentTransportRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private StudentTransportService studentTransportService;

    @Test
    @DisplayName("createStudentTransport: successfully creates assignment when all rules pass")
    void createStudentTransport_success() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setRouteId(3);
        request.setVehicleId(4);

        Route route = new Route();
        route.setId(3);
        route.setVendorId(2);
        route.setVehicleId(4);
        route.setDriverId(5);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(4);
        vehicle.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(routeRepository.findById(3)).thenReturn(Optional.of(route));
        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));

        when(studentTransportRepository.save(any(StudentTransport.class))).thenAnswer(invocation -> {
            StudentTransport st = invocation.getArgument(0);
            st.setId(10);
            return st;
        });

        StudentTransportResponse response = studentTransportService.createStudentTransport(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getStudentId()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getEnrollmentDate()).isEqualTo(LocalDate.now());

        verify(studentTransportRepository).save(any(StudentTransport.class));
    }

    @Test
    @DisplayName("createStudentTransport: throws ResourceNotFoundException when student not found")
    void createStudentTransport_studentNotFound() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(99);

        when(studentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentTransportService.createStudentTransport(request));
        verify(studentTransportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createStudentTransport: throws ResourceNotFoundException when vendor not found")
    void createStudentTransport_vendorNotFound() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(99);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentTransportService.createStudentTransport(request));
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when student already has active assignment")
    void createStudentTransport_activeAssignmentExists() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE"))
                .thenReturn(Optional.of(new StudentTransport()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("Student already has an active transport assignment");
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when route does not belong to vendor")
    void createStudentTransport_routeVendorMismatch() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setRouteId(3);

        Route route = new Route();
        route.setId(3);
        route.setVendorId(999); // different vendor

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(routeRepository.findById(3)).thenReturn(Optional.of(route));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("Route does not belong to vendor");
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when vehicle does not belong to vendor")
    void createStudentTransport_vehicleVendorMismatch() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setVehicleId(4);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(4);
        vehicle.setVendorId(999); // different vendor

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("Vehicle does not belong to vendor");
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when route has no vehicle assigned")
    void createStudentTransport_routeNoVehicle() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setRouteId(3);
        request.setVehicleId(4);

        Route route = new Route();
        route.setId(3);
        route.setVendorId(2);
        route.setVehicleId(null);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(4);
        vehicle.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(routeRepository.findById(3)).thenReturn(Optional.of(route));
        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("No vehicle assigned to route");
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when vehicle does not match route vehicle")
    void createStudentTransport_vehicleRouteMismatch() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setRouteId(3);
        request.setVehicleId(4);

        Route route = new Route();
        route.setId(3);
        route.setVendorId(2);
        route.setVehicleId(88); // route expects vehicle 88, but request gave 4

        Vehicle vehicle = new Vehicle();
        vehicle.setId(4);
        vehicle.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(routeRepository.findById(3)).thenReturn(Optional.of(route));
        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("Vehicle does not match selected route");
    }

    @Test
    @DisplayName("createStudentTransport: throws BadRequestException when route has no driver assigned")
    void createStudentTransport_routeNoDriver() {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setRouteId(3);
        request.setVehicleId(4);

        Route route = new Route();
        route.setId(3);
        route.setVendorId(2);
        route.setVehicleId(4);
        route.setDriverId(null);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(4);
        vehicle.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());
        when(routeRepository.findById(3)).thenReturn(Optional.of(route));
        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> studentTransportService.createStudentTransport(request));
        assertThat(ex.getMessage()).isEqualTo("No driver assigned to route");
    }

    @Test
    @DisplayName("getStudentTransportById: returns transport assignment when found")
    void getStudentTransportById_success() {
        StudentTransport transport = new StudentTransport();
        transport.setId(10);
        transport.setStudentId(1);

        when(studentTransportRepository.findById(10)).thenReturn(Optional.of(transport));

        StudentTransportResponse response = studentTransportService.getStudentTransportById(10);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
    }

    @Test
    @DisplayName("getStudentTransportById: throws ResourceNotFoundException when not found")
    void getStudentTransportById_notFound() {
        when(studentTransportRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentTransportService.getStudentTransportById(99));
    }

    @Test
    @DisplayName("getAllStudentTransport: returns all assignments")
    void getAllStudentTransport_success() {
        StudentTransport transport = new StudentTransport();
        transport.setId(1);

        when(studentTransportRepository.findAll()).thenReturn(List.of(transport));

        List<StudentTransportResponse> list = studentTransportService.getAllStudentTransport();

        assertThat(list).hasSize(1);
    }
}

package com.tgs.transport_governance_api.studenttransport.repository;

import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentTransportRepositoryTest {

    @Autowired
    private StudentTransportRepository studentTransportRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByStudentIdAndStatus: finds transport assignment by student ID and status")
    void findByStudentIdAndStatus_found() {
        StudentTransport transport = new StudentTransport();
        transport.setStudentId(1);
        transport.setVendorId(2);
        transport.setRouteId(3);
        transport.setVehicleId(4);
        transport.setStatus("ACTIVE");
        transport.setEnrollmentDate(LocalDate.now());
        transport.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(transport);

        Optional<StudentTransport> found = studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE");

        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo(1);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("findByStudentIdAndStatus: returns empty when status or student ID does not match")
    void findByStudentIdAndStatus_notFound() {
        StudentTransport transport = new StudentTransport();
        transport.setStudentId(1);
        transport.setVendorId(2);
        transport.setStatus("INACTIVE");
        transport.setEnrollmentDate(LocalDate.now());
        entityManager.persistAndFlush(transport);

        Optional<StudentTransport> active = studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE");
        Optional<StudentTransport> otherStudent = studentTransportRepository.findByStudentIdAndStatus(99, "INACTIVE");

        assertThat(active).isEmpty();
        assertThat(otherStudent).isEmpty();
    }
}


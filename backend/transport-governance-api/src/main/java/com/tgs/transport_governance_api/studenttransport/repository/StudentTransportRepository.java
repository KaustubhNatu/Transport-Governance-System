package com.tgs.transport_governance_api.studenttransport.repository;

import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentTransportRepository
        extends JpaRepository<StudentTransport, Integer> {

    Optional<StudentTransport> findByStudentIdAndStatus(
            Integer studentId,
            String status);

}
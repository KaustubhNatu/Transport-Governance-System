package com.tgs.transport_governance_api.students.repository;

import com.tgs.transport_governance_api.students.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<Student, Integer> {

    Optional<Student> findBySchoolIdAndParentIdAndName(
            Integer schoolId,
            Integer parentId,
            String name);

}
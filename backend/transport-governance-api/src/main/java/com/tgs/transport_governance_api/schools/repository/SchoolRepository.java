package com.tgs.transport_governance_api.schools.repository;

import com.tgs.transport_governance_api.schools.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Integer> {

}
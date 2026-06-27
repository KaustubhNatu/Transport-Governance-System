package com.tgs.transport_governance_api.parents.repository;

import com.tgs.transport_governance_api.parents.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository
        extends JpaRepository<Parent, Integer> {

    Optional<Parent> findByUserId(Integer userId);

}
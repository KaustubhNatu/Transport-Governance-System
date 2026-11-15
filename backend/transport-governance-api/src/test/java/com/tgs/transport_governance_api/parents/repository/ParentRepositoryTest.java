package com.tgs.transport_governance_api.parents.repository;

import com.tgs.transport_governance_api.parents.entity.Parent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByUserId: finds parent by user ID")
    void findByUserId_found() {
        Parent parent = new Parent();
        parent.setUserId(10);
        parent.setAddress("789 Maple Ave");
        parent.setEmergencyContact("555-0001");
        parent.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(parent);

        Optional<Parent> found = parentRepository.findByUserId(10);

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(10);
    }

    @Test
    @DisplayName("findByUserId: returns empty when user ID not found")
    void findByUserId_notFound() {
        Optional<Parent> found = parentRepository.findByUserId(999);
        assertThat(found).isEmpty();
    }
}


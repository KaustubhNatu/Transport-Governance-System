package com.tgs.transport_governance_api.users.repository;

import com.tgs.transport_governance_api.users.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByNameIgnoreCase: finds role regardless of case")
    void findByNameIgnoreCase_success() {
        Role role = new Role();
        role.setName("PARENT");
        role.setDescription("Parent role");
        entityManager.persistAndFlush(role);

        Optional<Role> exact = roleRepository.findByNameIgnoreCase("PARENT");
        Optional<Role> lower = roleRepository.findByNameIgnoreCase("parent");
        Optional<Role> mixed = roleRepository.findByNameIgnoreCase("PaReNt");

        assertThat(exact).isPresent();
        assertThat(lower).isPresent();
        assertThat(mixed).isPresent();
        assertThat(exact.get().getName()).isEqualTo("PARENT");
    }

    @Test
    @DisplayName("findByNameIgnoreCase: returns empty when role name not found")
    void findByNameIgnoreCase_notFound() {
        Optional<Role> role = roleRepository.findByNameIgnoreCase("UNKNOWN_ROLE");
        assertThat(role).isEmpty();
    }
}


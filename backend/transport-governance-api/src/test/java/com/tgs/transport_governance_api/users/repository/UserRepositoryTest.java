package com.tgs.transport_governance_api.users.repository;

import com.tgs.transport_governance_api.users.entity.Role;
import com.tgs.transport_governance_api.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByEmail: loads user along with eager roles via EntityGraph")
    void findByEmail_withRoles_found() {
        Role role = new Role();
        role.setName("SUPER_ADMIN");
        role.setDescription("Administrator");
        entityManager.persist(role);

        User user = new User();
        user.setEmail("admin@tgs.com");
        user.setPasswordHash("hashed123");
        user.setFirstName("Super");
        user.setLastName("Admin");
        user.setStatus("ACTIVE");
        user.setRoles(Set.of(role));
        entityManager.persistAndFlush(user);
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail("admin@tgs.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("admin@tgs.com");
        assertThat(found.get().getRoles()).hasSize(1);
        assertThat(found.get().getRoles().iterator().next().getName()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    @DisplayName("findByEmail: returns empty when email does not exist")
    void findByEmail_notFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@tgs.com");
        assertThat(found).isEmpty();
    }
}


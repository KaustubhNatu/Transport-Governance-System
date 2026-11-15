package com.tgs.transport_governance_api.schools.repository;

import com.tgs.transport_governance_api.schools.entity.School;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SchoolRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("save and findById: persists school and retrieves by primary key")
    void saveAndFindById_success() {
        School school = new School();
        school.setName("Oakridge Academy");
        school.setAddress("123 Oak Road");
        school.setContactEmail("contact@oakridge.edu");
        school.setCreatedAt(LocalDateTime.now());

        School saved = schoolRepository.save(school);

        assertThat(saved.getId()).isNotNull();

        Optional<School> found = schoolRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Oakridge Academy");
    }

    @Test
    @DisplayName("delete: deletes persisted school")
    void delete_success() {
        School school = new School();
        school.setName("Temporary School");
        school.setAddress("Temp Address");
        School saved = entityManager.persistAndFlush(school);

        schoolRepository.delete(saved);

        Optional<School> found = schoolRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}


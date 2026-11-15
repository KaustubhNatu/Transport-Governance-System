package com.tgs.transport_governance_api.students.repository;

import com.tgs.transport_governance_api.students.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findBySchoolIdAndParentIdAndName: returns student when exact match exists")
    void findBySchoolIdAndParentIdAndName_found() {
        // Arrange
        Student student = new Student();
        student.setSchoolId(1);
        student.setParentId(10);
        student.setName("Charlie Brown");
        student.setGrade("4");
        student.setSection("A");
        student.setStatus("ACTIVE");
        student.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(student);

        // Act
        Optional<Student> result = studentRepository.findBySchoolIdAndParentIdAndName(1, 10, "Charlie Brown");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Charlie Brown");
        assertThat(result.get().getGrade()).isEqualTo("4");
    }

    @Test
    @DisplayName("findBySchoolIdAndParentIdAndName: returns empty when parameters do not match")
    void findBySchoolIdAndParentIdAndName_notFound() {
        // Arrange
        Student student = new Student();
        student.setSchoolId(1);
        student.setParentId(10);
        student.setName("Charlie Brown");
        student.setStatus("ACTIVE");
        entityManager.persistAndFlush(student);

        // Act & Assert
        assertThat(studentRepository.findBySchoolIdAndParentIdAndName(2, 10, "Charlie Brown")).isEmpty();
        assertThat(studentRepository.findBySchoolIdAndParentIdAndName(1, 99, "Charlie Brown")).isEmpty();
        assertThat(studentRepository.findBySchoolIdAndParentIdAndName(1, 10, "Lucy Van Pelt")).isEmpty();
    }

    @Test
    @DisplayName("save: persists student and generates identity primary key")
    void save_persistsStudent() {
        // Arrange
        Student student = new Student();
        student.setSchoolId(5);
        student.setParentId(20);
        student.setName("Linus Van Pelt");
        student.setStatus("ACTIVE");
        student.setCreatedAt(LocalDateTime.now());

        // Act
        Student saved = studentRepository.save(student);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isPositive();

        Student retrieved = entityManager.find(Student.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("Linus Van Pelt");
    }

    @Test
    @DisplayName("save: throws DataIntegrityViolationException when nullable=false field is null")
    void save_violatesNotNullConstraint() {
        Student student = new Student();
        // name is nullable = false
        student.setName(null);
        student.setSchoolId(1);
        student.setParentId(1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            studentRepository.saveAndFlush(student);
        });
    }
}


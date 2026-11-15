package com.tgs.transport_governance_api.students.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.students.dto.CreateStudentRequest;
import com.tgs.transport_governance_api.students.dto.StudentResponse;
import com.tgs.transport_governance_api.students.entity.Student;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("createStudent: should successfully create and return student response when student is unique")
    void createStudent_success() {
        // Arrange
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Alice Smith");
        request.setGrade("5");
        request.setSection("B");

        when(studentRepository.findBySchoolIdAndParentIdAndName(1, 2, "Alice Smith"))
                .thenReturn(Optional.empty());

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(10);
            return s;
        });

        // Act
        StudentResponse response = studentService.createStudent(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getSchoolId()).isEqualTo(1);
        assertThat(response.getParentId()).isEqualTo(2);
        assertThat(response.getName()).isEqualTo("Alice Smith");
        assertThat(response.getGrade()).isEqualTo("5");
        assertThat(response.getSection()).isEqualTo("B");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getCreatedAt()).isNotNull();

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        Student captured = captor.getValue();
        assertThat(captured.getStatus()).isEqualTo("ACTIVE");
        assertThat(captured.getName()).isEqualTo("Alice Smith");
        verify(studentRepository).findBySchoolIdAndParentIdAndName(1, 2, "Alice Smith");
    }

    @Test
    @DisplayName("createStudent: should throw BadRequestException when student already exists")
    void createStudent_throwsBadRequestException_whenDuplicate() {
        // Arrange
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Alice Smith");

        Student existing = new Student();
        existing.setId(5);
        existing.setName("Alice Smith");

        when(studentRepository.findBySchoolIdAndParentIdAndName(1, 2, "Alice Smith"))
                .thenReturn(Optional.of(existing));

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> studentService.createStudent(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Student already exists");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("getStudentById: should return mapped StudentResponse when student exists")
    void getStudentById_success() {
        // Arrange
        Student student = new Student();
        student.setId(10);
        student.setSchoolId(1);
        student.setParentId(2);
        student.setName("Bob Jones");
        student.setGrade("8");
        student.setSection("C");
        student.setStatus("ACTIVE");
        student.setCreatedAt(LocalDateTime.now());

        when(studentRepository.findById(10)).thenReturn(Optional.of(student));

        // Act
        StudentResponse response = studentService.getStudentById(10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getName()).isEqualTo("Bob Jones");
        assertThat(response.getGrade()).isEqualTo("8");
        verify(studentRepository).findById(10);
    }

    @Test
    @DisplayName("getStudentById: should throw ResourceNotFoundException when student does not exist")
    void getStudentById_throwsResourceNotFoundException_whenNotFound() {
        // Arrange
        when(studentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudentById(999)
        );

        assertThat(exception.getMessage()).isEqualTo("Student not found");
        verify(studentRepository).findById(999);
    }

    @Test
    @DisplayName("getAllStudents: should return mapped list of all students")
    void getAllStudents_returnsMappedList() {
        // Arrange
        Student student1 = new Student();
        student1.setId(1);
        student1.setName("Student 1");

        Student student2 = new Student();
        student2.setId(2);
        student2.setName("Student 2");

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        // Act
        List<StudentResponse> responses = studentService.getAllStudents();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("Student 1");
        assertThat(responses.get(1).getName()).isEqualTo("Student 2");
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("getAllStudents: should return empty list when no students exist")
    void getAllStudents_returnsEmptyList() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<StudentResponse> responses = studentService.getAllStudents();

        // Assert
        assertThat(responses).isEmpty();
        verify(studentRepository).findAll();
    }
}


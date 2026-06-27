package com.tgs.transport_governance_api.students.service;

import com.tgs.transport_governance_api.students.dto.CreateStudentRequest;
import com.tgs.transport_governance_api.students.dto.StudentResponse;
import com.tgs.transport_governance_api.students.entity.Student;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public StudentResponse createStudent(CreateStudentRequest request) {

        if (studentRepository
                .findBySchoolIdAndParentIdAndName(
                        request.getSchoolId(),
                        request.getParentId(),
                        request.getName())
                .isPresent()) {

            throw new RuntimeException(
                    "Student already exists");
        }

        Student student = new Student();

        student.setSchoolId(request.getSchoolId());
        student.setParentId(request.getParentId());
        student.setName(request.getName());
        student.setGrade(request.getGrade());
        student.setSection(request.getSection());
        student.setStatus("ACTIVE");
        student.setCreatedAt(LocalDateTime.now());

        Student savedStudent =
                studentRepository.save(student);

        return mapToResponse(savedStudent);
    }

    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StudentResponse getStudentById(Integer id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"));

        return mapToResponse(student);
    }

    private StudentResponse mapToResponse(Student student) {

        StudentResponse response = new StudentResponse();

        response.setId(student.getId());
        response.setSchoolId(student.getSchoolId());
        response.setParentId(student.getParentId());
        response.setName(student.getName());
        response.setGrade(student.getGrade());
        response.setSection(student.getSection());
        response.setStatus(student.getStatus());
        response.setCreatedAt(student.getCreatedAt());

        return response;
    }
}
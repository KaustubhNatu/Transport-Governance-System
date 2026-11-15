package com.tgs.transport_governance_api.students.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.students.dto.CreateStudentRequest;
import com.tgs.transport_governance_api.students.dto.StudentResponse;
import com.tgs.transport_governance_api.students.service.StudentService;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import({StudentController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /students: creates student successfully when user has SCHOOL_ADMIN role")
    void createStudent_success_asSchoolAdmin() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("John Doe");
        request.setGrade("10");
        request.setSection("A");

        StudentResponse response = new StudentResponse();
        response.setId(101);
        response.setSchoolId(1);
        response.setParentId(2);
        response.setName("John Doe");
        response.setGrade("10");
        response.setSection("A");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());

        when(studentService.createStudent(any(CreateStudentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(studentService).createStudent(any(CreateStudentRequest.class));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("POST /students: creates student successfully when user has SUPER_ADMIN role")
    void createStudent_success_asSuperAdmin() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("John Doe");

        StudentResponse response = new StudentResponse();
        response.setId(102);
        response.setName("John Doe");
        response.setStatus("ACTIVE");

        when(studentService.createStudent(any(CreateStudentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(102))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /students: returns 400 Bad Request when validation fails on required fields")
    void createStudent_failsValidation_whenRequiredFieldsMissing() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        // Missing schoolId, parentId, and blank name
        request.setName("");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /students: returns 400 when service throws BadRequestException (e.g. duplicate)")
    void createStudent_returnsBadRequest_onDuplicateStudent() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Existing Student");

        when(studentService.createStudent(any(CreateStudentRequest.class)))
                .thenThrow(new BadRequestException("Student already exists"));

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Student already exists"));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /students: returns 403 Forbidden for unauthorized role VENDOR_ADMIN")
    void createStudent_forbidden_forVendorAdmin() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Unauthorized Student");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("POST /students: returns 403 Forbidden for unauthorized role PARENT")
    void createStudent_forbidden_forParent() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Unauthorized Student");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /students: returns 403 Forbidden when unauthenticated")
    void createStudent_unauthorized_whenUnauthenticated() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setSchoolId(1);
        request.setParentId(2);
        request.setName("Unauthenticated Student");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /students/{id}: returns student response when student exists")
    void getStudentById_success() throws Exception {
        StudentResponse response = new StudentResponse();
        response.setId(10);
        response.setName("Jane Doe");
        response.setGrade("6");
        response.setStatus("ACTIVE");

        when(studentService.getStudentById(10)).thenReturn(response);

        mockMvc.perform(get("/students/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.grade").value("6"));

        verify(studentService).getStudentById(10);
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /students/{id}: returns 404 Not Found when student does not exist")
    void getStudentById_notFound() throws Exception {
        when(studentService.getStudentById(999))
                .thenThrow(new ResourceNotFoundException("Student not found"));

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Student not found"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /students: returns all students successfully")
    void getAllStudents_success() throws Exception {
        StudentResponse response1 = new StudentResponse();
        response1.setId(1);
        response1.setName("Student 1");

        StudentResponse response2 = new StudentResponse();
        response2.setId(2);
        response2.setName("Student 2");

        when(studentService.getAllStudents()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Student 1"))
                .andExpect(jsonPath("$[1].name").value("Student 2"));

        verify(studentService).getAllStudents();
    }

    @Test
    @DisplayName("GET /students: returns 403 Forbidden when unauthenticated")
    void getAllStudents_unauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("GET /students: returns 403 Forbidden for VENDOR_ADMIN")
    void getAllStudents_forbidden_forVendorAdmin() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isForbidden());
    }
}


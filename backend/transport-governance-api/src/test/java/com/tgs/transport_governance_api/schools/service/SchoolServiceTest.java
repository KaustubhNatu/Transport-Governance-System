package com.tgs.transport_governance_api.schools.service;

import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.schools.dto.CreateSchoolRequest;
import com.tgs.transport_governance_api.schools.dto.SchoolResponse;
import com.tgs.transport_governance_api.schools.entity.School;
import com.tgs.transport_governance_api.schools.repository.SchoolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @InjectMocks
    private SchoolService schoolService;

    @Test
    @DisplayName("createSchool: creates and returns school response with timestamp")
    void createSchool_success() {
        CreateSchoolRequest request = new CreateSchoolRequest();
        request.setName("Springfield High");
        request.setAddress("742 Evergreen Terrace");
        request.setContactEmail("info@springfield.edu");
        request.setContactPhone("555-1234");

        when(schoolRepository.save(any(School.class))).thenAnswer(invocation -> {
            School s = invocation.getArgument(0);
            s.setId(1);
            return s;
        });

        SchoolResponse response = schoolService.createSchool(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Springfield High");
        assertThat(response.getContactEmail()).isEqualTo("info@springfield.edu");
        assertThat(response.getCreatedAt()).isNotNull();

        ArgumentCaptor<School> captor = ArgumentCaptor.forClass(School.class);
        verify(schoolRepository).save(captor.capture());
        assertThat(captor.getValue().getAddress()).isEqualTo("742 Evergreen Terrace");
    }

    @Test
    @DisplayName("getAllSchools: returns list of mapped schools")
    void getAllSchools_success() {
        School school = new School();
        school.setId(1);
        school.setName("School 1");

        when(schoolRepository.findAll()).thenReturn(List.of(school));

        List<SchoolResponse> list = schoolService.getAllSchools();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("School 1");
    }

    @Test
    @DisplayName("getSchoolById: returns school when found")
    void getSchoolById_success() {
        School school = new School();
        school.setId(1);
        school.setName("School 1");

        when(schoolRepository.findById(1)).thenReturn(Optional.of(school));

        SchoolResponse response = schoolService.getSchoolById(1);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getSchoolById: throws ResourceNotFoundException when not found")
    void getSchoolById_throwsNotFound() {
        when(schoolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> schoolService.getSchoolById(99));
    }

    @Test
    @DisplayName("updateSchool: updates existing school and returns mapped response")
    void updateSchool_success() {
        School existing = new School();
        existing.setId(1);
        existing.setName("Old Name");
        existing.setAddress("Old Address");

        CreateSchoolRequest updateReq = new CreateSchoolRequest();
        updateReq.setName("New Name");
        updateReq.setAddress("New Address");
        updateReq.setContactEmail("new@school.com");
        updateReq.setContactPhone("555-9999");

        when(schoolRepository.findById(1)).thenReturn(Optional.of(existing));
        when(schoolRepository.save(existing)).thenReturn(existing);

        SchoolResponse response = schoolService.updateSchool(1, updateReq);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getAddress()).isEqualTo("New Address");
        verify(schoolRepository).save(existing);
    }

    @Test
    @DisplayName("updateSchool: throws ResourceNotFoundException when school does not exist")
    void updateSchool_throwsNotFound() {
        when(schoolRepository.findById(99)).thenReturn(Optional.empty());

        CreateSchoolRequest updateReq = new CreateSchoolRequest();
        assertThrows(ResourceNotFoundException.class, () -> schoolService.updateSchool(99, updateReq));
        verify(schoolRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteSchool: deletes school when found")
    void deleteSchool_success() {
        School school = new School();
        school.setId(1);

        when(schoolRepository.findById(1)).thenReturn(Optional.of(school));

        schoolService.deleteSchool(1);

        verify(schoolRepository).delete(school);
    }

    @Test
    @DisplayName("deleteSchool: throws ResourceNotFoundException when school not found")
    void deleteSchool_throwsNotFound() {
        when(schoolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> schoolService.deleteSchool(99));
        verify(schoolRepository, never()).delete(any());
    }
}


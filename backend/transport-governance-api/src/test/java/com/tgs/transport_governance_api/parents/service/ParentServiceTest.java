package com.tgs.transport_governance_api.parents.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.parents.dto.CreateParentRequest;
import com.tgs.transport_governance_api.parents.dto.ParentResponse;
import com.tgs.transport_governance_api.parents.entity.Parent;
import com.tgs.transport_governance_api.parents.repository.ParentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private ParentService parentService;

    @Test
    @DisplayName("createParent: successfully creates parent when user ID is unique")
    void createParent_success() {
        CreateParentRequest request = new CreateParentRequest();
        request.setUserId(10);
        request.setAddress("456 Elm St");
        request.setEmergencyContact("999-888-7777");

        when(parentRepository.findByUserId(10)).thenReturn(Optional.empty());
        when(parentRepository.save(any(Parent.class))).thenAnswer(invocation -> {
            Parent p = invocation.getArgument(0);
            p.setId(1);
            return p;
        });

        ParentResponse response = parentService.createParent(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getUserId()).isEqualTo(10);
        assertThat(response.getAddress()).isEqualTo("456 Elm St");
        verify(parentRepository).save(any(Parent.class));
    }

    @Test
    @DisplayName("createParent: throws BadRequestException when user ID already associated with a parent")
    void createParent_throwsBadRequestException_whenDuplicate() {
        CreateParentRequest request = new CreateParentRequest();
        request.setUserId(10);

        when(parentRepository.findByUserId(10)).thenReturn(Optional.of(new Parent()));

        assertThrows(BadRequestException.class, () -> parentService.createParent(request));
        verify(parentRepository, never()).save(any());
    }

    @Test
    @DisplayName("getParentById: returns parent when found")
    void getParentById_success() {
        Parent parent = new Parent();
        parent.setId(1);
        parent.setUserId(10);

        when(parentRepository.findById(1)).thenReturn(Optional.of(parent));

        ParentResponse response = parentService.getParentById(1);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getUserId()).isEqualTo(10);
    }

    @Test
    @DisplayName("getParentById: throws ResourceNotFoundException when not found")
    void getParentById_notFound() {
        when(parentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> parentService.getParentById(99));
    }

    @Test
    @DisplayName("getAllParents: returns list of all parents")
    void getAllParents_success() {
        Parent parent = new Parent();
        parent.setId(1);

        when(parentRepository.findAll()).thenReturn(List.of(parent));

        List<ParentResponse> list = parentService.getAllParents();

        assertThat(list).hasSize(1);
    }
}


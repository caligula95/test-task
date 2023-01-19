package com.tui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.model.Repository;
import com.tui.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.tui.prototype.RepositoryPrototype.aRepository;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RepositoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        repositoryService = mock(RepositoryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RepositoryController(repositoryService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRepositories() throws Exception {
        List<Repository> repositories = List.of(aRepository(), aRepository());
        when(repositoryService.getNonForkRepositoriesByUsername(any())).thenReturn(repositories);
        mockMvc.perform(get("/users/testUser/repositories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(repositories)))
                .andExpect(status().isOk());
        verify(repositoryService).getNonForkRepositoriesByUsername("testUser");
    }

    @Test
    void getRepositoriesReturnsEmptyListIfNoResponse() throws Exception {
        List<Repository> repositories = List.of();
        when(repositoryService.getNonForkRepositoriesByUsername(any())).thenReturn(repositories);
        mockMvc.perform(get("/users/testUser/repositories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(repositories)))
                .andExpect(status().isOk());
    }
}
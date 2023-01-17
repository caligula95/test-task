package com.tui.integration;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class RepositoryControllerIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldGetUserRepositories() throws Exception {
        List<GithubBranch> githubBranches = List.of(aGithubBranch(),
                aGithubBranch());
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubBranch>>>any())
        ).thenReturn(ResponseEntity.ok(githubBranches))
                .thenReturn(ResponseEntity.ok(List.of(aGithubBranch())))
                .thenReturn(ResponseEntity.ok(List.of()));

        List<GithubRepository> githubRepositoriesResponse = List.of(aGithubRepository(false),
                aGithubRepository(true));
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(githubRepositoriesResponse))
                .thenReturn(ResponseEntity.ok(githubRepositoriesResponse))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(
                        get("/repositories?username=testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", equalTo("repoNamefalse")))
                .andExpect(jsonPath("$.[0].ownerLogin", equalTo("ownerLogin")));

    }

    @Test
    void shouldReturnUserRepositoriesEmptyIfAllFork() throws Exception {
        List<GithubRepository> githubRepositoriesResponse = List.of(aGithubRepository(true),
                aGithubRepository(true));
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(githubRepositoriesResponse))
                .thenReturn(ResponseEntity.ok(githubRepositoriesResponse))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(
                        get("/repositories?username=testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0)));

    }

    @Test
    void shouldReturnErrorMessageInCaseOfClientException() throws Exception {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenThrow(new HttpClientErrorException(HttpStatus.valueOf(403), "Limit exceeded"));
        mockMvc.perform(
                        get("/repositories?username=testUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.status", equalTo(403)))
                .andExpect(jsonPath("$.Message", equalTo("403 Limit exceeded")));
    }

    @Test
    void shouldReturnErrorMessageInCaseOfIncorrectAcceptHeaderValue() throws Exception {
        mockMvc.perform(
                        get("/repositories?username=testUser")
                                .accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.Message", equalTo("Could not find acceptable representation")));
    }

    @Test
    void shouldReturnBadRequestIfUsernameIsMissing() throws Exception {
        mockMvc.perform(
                        get("/repositories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundIfUserNotExists() throws Exception {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenThrow(new HttpClientErrorException(HttpStatus.valueOf(404), "User doesn't exist"));
        mockMvc.perform(
                        get("/repositories?username=not_existing_user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.Message", equalTo("404 User doesn't exist")));
    }
}

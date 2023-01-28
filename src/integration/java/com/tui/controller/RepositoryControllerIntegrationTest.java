package com.tui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tui.prototype.GithubResponsePrototype.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class RepositoryControllerIntegrationTest {

    private static WireMockServer wireMockServer;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeAll
    static void before() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(7070)
        );
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterAll
    static void shutDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldGetUserRepositories() throws Exception {
        List<GithubBranch> githubBranches = List.of(aGithubBranch(),
                aGithubBranch());

        List<GithubRepository> githubRepositoriesResponse = List.of(aGithubRepository(false),
                aGithubRepository(true));

        createStub("/users/testUser/repos", "1", objectMapper.writeValueAsString(githubRepositoriesResponse));
        createStub("/users/testUser/repos", "2", objectMapper.writeValueAsString(List.of()));
        createStub("/repos/testUser/repoNamefalse/branches", "1", objectMapper.writeValueAsString(githubBranches));
        createStub("/repos/testUser/repoNamefalse/branches", "2", objectMapper.writeValueAsString(List.of()));

        mockMvc.perform(
                        get("/users/testUser/repositories")
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
        createStub("/users/testUser/repos", "1", objectMapper.writeValueAsString(githubRepositoriesResponse));
        createStub("/users/testUser/repos", "2", objectMapper.writeValueAsString(List.of()));

        mockMvc.perform(
                        get("/users/testUser/repositories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0)));

    }

    @Test
    void shouldReturnErrorMessageInCaseOfClientException() throws Exception {
        stubFor(WireMock.get(urlPathMatching("/users/testUser/repos"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")));
        mockMvc.perform(
                        get("/users/testUser/repositories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.status", equalTo(403)));
    }

    @Test
    void shouldReturnErrorMessageInCaseOfIncorrectAcceptHeaderValue() throws Exception {
        mockMvc.perform(
                        get("/users/testUser/repositories")
                                .accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().is(HttpStatus.NOT_ACCEPTABLE.value()))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.Message", equalTo("Could not find acceptable representation")));
    }

    @Test
    void shouldReturnNotFoundIfUserNotExists() throws Exception {

        stubFor(WireMock.get(urlPathMatching("/users/testUser/repos"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")));

        mockMvc.perform(
                        get("/users/testUser/repositories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.value())));
    }

    private void createStub(String urlPattern, String pageNumber, String bodyString) {
        stubFor(WireMock.get(urlPathMatching(urlPattern))
                .withQueryParam("page", matching(pageNumber))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(bodyString)));
    }
}

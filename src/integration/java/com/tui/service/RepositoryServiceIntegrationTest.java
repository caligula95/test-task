package com.tui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import com.tui.model.Repository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles({"test"})
public class RepositoryServiceIntegrationTest {

    private static WireMockServer wireMockServer;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RepositoryService repositoryService;

    @BeforeAll
    static void before() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(7070)
        );
        wireMockServer.start();
        WireMock.configureFor("localhost", 7070);
    }

    @AfterAll
    static void shutDown() {
        wireMockServer.stop();
    }

    @Test
    void getRepositoriesByUsername() throws Exception {
        List<GithubRepository> githubRepositoriesResponse = List.of(aGithubRepository(false),
                aGithubRepository(true));
        List<GithubBranch> githubBranches = List.of(aGithubBranch(),
                aGithubBranch());

        createStub("/users/username/repos", "1", objectMapper.writeValueAsString(githubRepositoriesResponse));
        createStub("/users/username/repos", "2", objectMapper.writeValueAsString(List.of()));
        createStub("/repos/username/repoNamefalse/branches", "1", objectMapper.writeValueAsString(githubBranches));
        createStub("/repos/username/repoNamefalse/branches", "2", objectMapper.writeValueAsString(List.of()));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.get(0).getName()).isEqualTo("repoNamefalse");
    }

    @Test
    void getRepositoriesReturnUserRepositoriesEmptyIfAllFork() throws Exception {
        List<GithubRepository> githubRepositoriesResponse = List.of(aGithubRepository(true),
                aGithubRepository(true));

        createStub("/users/username/repos", "1", objectMapper.writeValueAsString(githubRepositoriesResponse));
        createStub("/users/username/repos", "2", objectMapper.writeValueAsString(List.of()));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isZero();
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

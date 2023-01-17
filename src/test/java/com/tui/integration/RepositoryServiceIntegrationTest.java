package com.tui.integration;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import com.tui.model.Repository;
import com.tui.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test"})
public class RepositoryServiceIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    void getRepositoriesByUsername() {
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

        List<Repository> repositories = repositoryService.getRepositoriesByUsername("username");
        assertThat(repositories).isNotNull();
        assertThat(repositories.get(0).getName()).isEqualTo("repoNamefalse");
    }

    @Test
    void getRepositoriesReturnUserRepositoriesEmptyIfAllFork() {
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

        List<Repository> repositories = repositoryService.getRepositoriesByUsername("username");
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isZero();
    }
}

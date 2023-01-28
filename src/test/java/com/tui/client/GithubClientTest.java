package com.tui.client;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import com.tui.config.GithubProperties;
import com.tui.exception.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GithubClientTest {

    private RestTemplate restTemplate;

    private GithubClient githubClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        GithubProperties githubProperties = new GithubProperties();
        githubProperties.setUrl("http://githubUrl.com");
        githubClient = new GithubClient(restTemplate, githubProperties);
    }

    @Test
    void getAllRepositoriesByUsername() {
        List<GithubRepository> githubRepositories = List.of(aGithubRepository(false),
                aGithubRepository(false));
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(githubRepositories))
                .thenReturn(ResponseEntity.ok(List.of(aGithubRepository(false))))
                .thenReturn(ResponseEntity.ok(List.of()));

        List<GithubRepository> githubResponse = githubClient.getAllRepositoriesByUsername("username");
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isEqualTo(3);
    }

    @Test
    void getAllRepositoriesByUsernameReturnsEmptyListIfBodyIsEmpty() {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(List.of()));

        List<GithubRepository> githubResponse = githubClient.getAllRepositoriesByUsername("username");
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isZero();
    }

    @Test
    void getRepositoriesByUsernameThrowsClientExceptionWhenNotSucceededResponse() {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.badRequest().build());

        assertThrows(ClientException.class,
                () -> githubClient.getAllRepositoriesByUsername("username"));
    }

    @Test
    void getAllBranchesByRepositoryAndUserName() {
        List<GithubBranch> githubBranches = List.of(aGithubBranch(), aGithubBranch());
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubBranch>>>any())
        ).thenReturn(ResponseEntity.ok(githubBranches))
                .thenReturn(ResponseEntity.ok(List.of(aGithubBranch())))
                .thenReturn(ResponseEntity.ok(List.of()));

        List<GithubBranch> githubResponse = githubClient
                .getAllBranchesByRepositoryAndUserName("repositoryName", "username");
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isEqualTo(3);
    }

    @Test
    void getBranchesByRepositoryAndUserNameThrowsClientExceptionWhenNotSucceededResponse() {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubBranch>>>any())
        ).thenReturn(ResponseEntity.badRequest().build());

        assertThrows(ClientException.class,
                () -> githubClient.getAllBranchesByRepositoryAndUserName("repositoryName", "username"));
    }

    @Test
    void getBranchesByRepositoryAndUserNameReturnsEmptyListIfBodyIsEmpty() {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubBranch>>>any())
        ).thenReturn(ResponseEntity.ok(List.of()));

        List<GithubBranch> githubResponse = githubClient
                .getAllBranchesByRepositoryAndUserName("repositoryName", "username");
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isZero();
    }
}
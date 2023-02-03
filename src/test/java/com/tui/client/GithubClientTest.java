package com.tui.client;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import com.tui.config.GithubProperties;
import com.tui.exception.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.tui.client.GithubClient.*;
import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GithubClientTest {

    private RestTemplate restTemplate;
    private GithubClient githubClient;
    private GithubProperties githubProperties;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        githubProperties = new GithubProperties();
        githubProperties.setUrl("http://githubUrl.com");
        githubClient = new GithubClient(restTemplate, githubProperties);
    }

    @Test
    void getRepositoriesByUsername() {
        List<GithubRepository> githubRepositories = List.of(aGithubRepository(false),
                aGithubRepository(false));

        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(githubRepositories));

        List<GithubRepository> githubResponse = githubClient.getRepositoriesByUsername("username", 1, 100);
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isEqualTo(githubRepositories.size());

        String requestString = UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl() + "/users/username/repos")
                .queryParam(TYPE, SOURCE)
                .queryParam(PAGE, 1)
                .queryParam(PER_PAGE, 100)
                .toUriString();

        verify(restTemplate)
                .exchange(requestString,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders()),
                        new ParameterizedTypeReference<java.util.List<com.tui.client.response.GithubRepository>>() {
                        });
    }

    @Test
    void getRepositoriesByUsernameReturnsEmptyListIfBodyIsEmpty() {
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubRepository>>>any())
        ).thenReturn(ResponseEntity.ok(List.of()));

        List<GithubRepository> githubResponse = githubClient.getRepositoriesByUsername("username", 1, 100);
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isZero();

        String requestString = UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl() + "/users/username/repos")
                .queryParam(TYPE, SOURCE)
                .queryParam(PAGE, 1)
                .queryParam(PER_PAGE, 100)
                .toUriString();

        verify(restTemplate)
                .exchange(requestString,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders()),
                        new ParameterizedTypeReference<java.util.List<com.tui.client.response.GithubRepository>>() {
                        });
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
                () -> githubClient.getRepositoriesByUsername("username", 1, 100));
    }

    @Test
    void getBranchesByRepositoryAndUserName() {
        List<GithubBranch> githubBranches = List.of(aGithubBranch(), aGithubBranch());
        when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<ParameterizedTypeReference<List<GithubBranch>>>any())
        ).thenReturn(ResponseEntity.ok(githubBranches));

        List<GithubBranch> githubResponse = githubClient
                .getBranchesByRepositoryAndUserName("repositoryName", "username", 1, 100);
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isEqualTo(githubBranches.size());

        String requestString = UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl()
                        + "/repos/username/repositoryName/branches")
                .queryParam(PAGE, 1)
                .queryParam(PER_PAGE, 100)
                .toUriString();

        verify(restTemplate)
                .exchange(requestString,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders()),
                        new ParameterizedTypeReference<java.util.List<com.tui.client.response.GithubBranch>>() {
                        });
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
                () -> githubClient.getBranchesByRepositoryAndUserName("repositoryName", "username", 1, 100));
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
                .getBranchesByRepositoryAndUserName("repositoryName", "username", 1, 100);
        assertThat(githubResponse).isNotNull();
        assertThat(githubResponse.size()).isZero();

        String requestString = UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl()
                        + "/repos/username/repositoryName/branches")
                .queryParam(PAGE, 1)
                .queryParam(PER_PAGE, 100)
                .toUriString();

        verify(restTemplate)
                .exchange(requestString,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders()),
                        new ParameterizedTypeReference<java.util.List<com.tui.client.response.GithubBranch>>() {
                        });
    }
}
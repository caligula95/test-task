package com.tui.client;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubRepository;
import com.tui.config.GithubProperties;
import com.tui.exception.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubClient {

    public static final int FIRST_PAGE = 1;

    private final RestTemplate restTemplate;
    private final GithubProperties githubProperties;

    public List<GithubRepository> getRepositoriesByUsername(String username) {
        return getAllUserRepositories(FIRST_PAGE, username, new ArrayList<>());
    }

    public List<GithubBranch> getBranchesByRepositoryAndUserName(String repositoryName, String username) {
        return getAllRepoBranches(1, repositoryName, username, new ArrayList<>());
    }

    private List<GithubBranch> getAllRepoBranches(int pageCount, String repositoryName,
                                                  String username, List<GithubBranch> allBranches) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<List<GithubBranch>> response = restTemplate.exchange(prepareGetBranchesRequest(pageCount, repositoryName, username),
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to get branches: {}, {}", response.getStatusCode(), response.getBody());
            throw new ClientException("Github get branches request failed: " + response.getStatusCode());
        }

        List<GithubBranch> responseBody = response.getBody();
        if (responseBody != null && !responseBody.isEmpty()) {
            allBranches.addAll(responseBody);
            getAllRepoBranches(++pageCount, repositoryName, username, allBranches);
        }
        return allBranches;
    }

    private List<GithubRepository> getAllUserRepositories(int pageCount, String username,
                                                          List<GithubRepository> allRepos) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<List<GithubRepository>> response = restTemplate.exchange(prepareGetRepositoriesRequest(pageCount, username),
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to get repositories: {}, {}", response.getStatusCode(), response.getBody());
            throw new ClientException("Github get repositories request failed: " + response.getStatusCode());
        }

        List<GithubRepository> responseBody = response.getBody();
        if (responseBody != null && !responseBody.isEmpty()) {
            allRepos.addAll(responseBody);
            getAllUserRepositories(++pageCount, username, allRepos);
        }
        return allRepos;
    }

    private String prepareGetRepositoriesRequest(int pageCount, String username) {
        return UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl() + "/users/" + username + "/repos")
                .queryParam("type", "source")
                .queryParam("page", pageCount)
                .toUriString();
    }

    private String prepareGetBranchesRequest(int pageCount, String repositoryName, String username) {
        return UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl()
                        + "/repos/" + username + "/" + repositoryName + "/branches")
                .queryParam("page", pageCount)
                .toUriString();
    }
}

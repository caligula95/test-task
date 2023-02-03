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

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubClient {

    public static final String PAGE = "page";
    public static final String PER_PAGE = "per_page";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";

    private final RestTemplate restTemplate;
    private final GithubProperties githubProperties;

    /**
     * Returns github repos by username
     *
     * @param username username in github
     * @return list of {@link GithubRepository}
     */
    public List<GithubRepository> getRepositoriesByUsername(String username, int page, int pageSize) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<List<GithubRepository>> response = restTemplate.exchange(prepareGetRepositoriesRequest(page, pageSize, username),
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to get repositories: {}, {}", response.getStatusCode(), response.getBody());
            throw new ClientException("Github get repositories request failed: " + response.getStatusCode());
        }

        return response.getBody();
    }

    /**
     * Returns github branches by username and repo name
     *
     * @param repositoryName name of the repository
     * @param username       username in github
     * @return list of {@link GithubBranch}
     */
    public List<GithubBranch> getBranchesByRepositoryAndUserName(String repositoryName, String username, int page, int pageSize) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<List<GithubBranch>> response = restTemplate.exchange(prepareGetBranchesRequest(page, pageSize, repositoryName, username),
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to get repositories: {}, {}", response.getStatusCode(), response.getBody());
            throw new ClientException("Github get repositories request failed: " + response.getStatusCode());
        }

        return response.getBody();
    }

    private String prepareGetRepositoriesRequest(int page, int perPage, String username) {
        return UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl() + "/users/" + username + "/repos")
                .queryParam(TYPE, SOURCE)
                .queryParam(PAGE, page)
                .queryParam(PER_PAGE, perPage)
                .toUriString();
    }

    private String prepareGetBranchesRequest(int page, int perPage, String repositoryName, String username) {
        return UriComponentsBuilder.fromHttpUrl(githubProperties.getUrl()
                        + "/repos/" + username + "/" + repositoryName + "/branches")
                .queryParam(PAGE, page)
                .queryParam(PER_PAGE, perPage)
                .toUriString();
    }
}

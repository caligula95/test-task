package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class DefaultRepositoryServiceTest {

    private GithubClient githubClient;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        githubClient = mock(GithubClient.class);
        repositoryService = new DefaultRepositoryService(githubClient);
    }

    @Test
    void getRepositoriesByUsername() {
        when(githubClient.getBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of(aGithubBranch()));
        when(githubClient.getRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(true),
                        aGithubRepository(false)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsername("username");
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isOne();
        assertThat(repositories.get(0).getName()).isEqualTo("repoNamefalse");
        assertThat(repositories.get(0).getBranches().size()).isEqualTo(1);
    }

    @Test
    void getRepositoriesByUsernameIsemptyIfAllFork() {
        when(githubClient.getBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of(aGithubBranch()));
        when(githubClient.getRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(true),
                        aGithubRepository(true)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsername("username");
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isZero();
    }

    @Test
    void getRepositoriesByUsernameBranchesAreEmptyIfNotPresentInResponse() {
        when(githubClient.getBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of());
        when(githubClient.getRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(false),
                        aGithubRepository(false)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsername("username");
        assertThat(repositories.get(0).getBranches().size()).isZero();
    }
}
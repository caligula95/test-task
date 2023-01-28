package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.converter.GithubConverter;
import com.tui.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class GithubRepositoryServiceTest {

    private GithubClient githubClient;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        githubClient = mock(GithubClient.class);
        GithubConverter githubConverter = new GithubConverter();
        repositoryService = new GithubRepositoryService(githubClient, githubConverter);
    }

    @Test
    void getRepositoriesByUsername() {
        when(githubClient.getAllBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of(aGithubBranch()));
        when(githubClient.getAllRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(true),
                        aGithubRepository(false)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isOne();
        assertThat(repositories.get(0).getName()).isEqualTo("repoNamefalse");
        assertThat(repositories.get(0).getBranches().size()).isEqualTo(1);
    }

    @Test
    void getRepositoriesByUsernameIsemptyIfAllFork() {
        when(githubClient.getAllBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of(aGithubBranch()));
        when(githubClient.getAllRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(true),
                        aGithubRepository(true)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isZero();
    }

    @Test
    void getRepositoriesByUsernameBranchesAreEmptyIfNotPresentInResponse() {
        when(githubClient.getAllBranchesByRepositoryAndUserName(any(), any()))
                .thenReturn(List.of());
        when(githubClient.getAllRepositoriesByUsername(any()))
                .thenReturn(List.of(aGithubRepository(false),
                        aGithubRepository(false)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories.get(0).getBranches().size()).isZero();
    }
}
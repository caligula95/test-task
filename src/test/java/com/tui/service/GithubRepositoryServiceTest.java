package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.mapper.GithubRepositoryMapper;
import com.tui.model.Branch;
import com.tui.model.Repository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.aGithubRepository;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class GithubRepositoryServiceTest {

    private GithubClient githubClient;
    private BranchService branchService;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        githubClient = mock(GithubClient.class);
        branchService = mock(BranchService.class);
        GithubRepositoryMapper githubRepositoryMapper = new GithubRepositoryMapper();
        repositoryService =
                new GithubRepositoryService(githubClient, githubRepositoryMapper, branchService);
    }

    @Test
    void getRepositoriesByUsername() {
        when(githubClient.getRepositoriesByUsername(eq("username"), eq(1), eq(100)))
                .thenReturn(List.of(aGithubRepository(true), aGithubRepository(false)));

        when(githubClient.getRepositoriesByUsername(eq("username"), eq(2), eq(100)))
                .thenReturn(List.of(aGithubRepository(true), aGithubRepository(false)));

        when(githubClient.getRepositoriesByUsername(eq("username"), eq(3), eq(100)))
                .thenReturn(List.of(aGithubRepository(true), aGithubRepository(false)));

        when(branchService.getBranchesByRepositoryAndUsername(anyString(), anyString()))
                .thenReturn(List.of(new Branch("name1", "commitSha1"),
                        new Branch("name2", "commitSha2")));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isEqualTo(3);
        assertThat(repositories.get(0).getBranches().size()).isEqualTo(2);

        Assertions.assertThat(repositories).extracting("name").contains("repoNamefalse");
        Assertions.assertThat(repositories).extracting("ownerLogin").contains("ownerLogin");

        verify(githubClient, times(4)).getRepositoriesByUsername(eq("username"), anyInt(), eq(100));
    }

    @Test
    void getRepositoriesByUsernameIsemptyIfAllFork() {
        when(githubClient.getRepositoriesByUsername(eq("username"), eq(1), eq(100)))
                .thenReturn(List.of(aGithubRepository(true), aGithubRepository(true)));

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);
        assertThat(repositories).isNotNull();
        assertThat(repositories.size()).isZero();

        verify(githubClient).getRepositoriesByUsername(eq("username"), eq(1), eq(100));
        verifyNoInteractions(branchService);
    }

    @Test
    void getRepositoriesByUsernameBranchesAreEmptyIfNotPresentInResponse() {
        when(githubClient.getRepositoriesByUsername(eq("username"), eq(1), eq(100)))
                .thenReturn(List.of(aGithubRepository(false), aGithubRepository(false)));

        when(branchService.getBranchesByRepositoryAndUsername(anyString(), anyString()))
                .thenReturn(List.of());

        List<Repository> repositories = repositoryService.getRepositoriesByUsernameAndForkParam("username", false);

        Assertions.assertThat(repositories)
                .flatExtracting("branches").isEmpty();

        verify(githubClient).getRepositoriesByUsername(eq("username"), eq(1), eq(100));
        verify(branchService, times(2)).getBranchesByRepositoryAndUsername(anyString(), eq("username"));
    }
}
package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.mapper.GithubBranchMapper;
import com.tui.model.Branch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tui.prototype.GithubResponsePrototype.aGithubBranch;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GithubBranchServiceTest {

    private GithubClient githubClient;

    private BranchService branchService;

    @BeforeEach
    void setUp() {
        githubClient = mock(GithubClient.class);
        branchService = new GithubBranchService(githubClient, new GithubBranchMapper());
    }

    @Test
    void getBranchesByRepositoryAndUsername() {
        when(githubClient.getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"), eq(1), eq(100)))
                .thenReturn(List.of(aGithubBranch(), aGithubBranch()));

        when(githubClient.getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"), eq(2), eq(100)))
                .thenReturn(List.of(aGithubBranch(), aGithubBranch()));

        when(githubClient.getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"), eq(3), eq(100)))
                .thenReturn(List.of(aGithubBranch(), aGithubBranch()));

        List<Branch> branches = branchService.getBranchesByRepositoryAndUsername("repositoryname",
                "username");
        assertThat(branches).isNotNull();
        assertThat(branches.size()).isEqualTo(6);
        assertThat(branches.get(0).getName()).isEqualTo("name");

        verify(githubClient, times(4))
                .getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"),
                        anyInt(), eq(100));
    }

    @Test
    void getBranchesByRepositoryAndUsernameAreEmptyIfNotPresentInResponse() {
        when(githubClient.getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"), eq(1), eq(100)))
                .thenReturn(List.of());

        List<Branch> branches = branchService.getBranchesByRepositoryAndUsername("repositoryname",
                "username");
        assertThat(branches.size()).isZero();

        verify(githubClient).getBranchesByRepositoryAndUserName(eq("repositoryname"), eq("username"), eq(1), eq(100));
    }
}
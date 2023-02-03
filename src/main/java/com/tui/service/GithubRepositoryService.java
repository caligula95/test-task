package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.client.response.GithubRepository;
import com.tui.mapper.GithubRepositoryMapper;
import com.tui.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService implements RepositoryService {

    private final GithubClient githubClient;
    private final GithubRepositoryMapper githubRepositoryMapper;
    private final BranchService branchService;

    @Override
    public List<Repository> getRepositoriesByUsernameAndForkParam(String username, boolean isFork) {
        List<GithubRepository> allRepos = new ArrayList<>();
        int page = 1;
        int pageSize = 100;
        List<GithubRepository> githubResponse = githubClient.getRepositoriesByUsername(username, page, pageSize);

        while (githubResponse != null && !githubResponse.isEmpty()) {
            allRepos.addAll(githubResponse);
            githubResponse = githubClient.getRepositoriesByUsername(username, ++page, pageSize);
        }

        return allRepos.stream()
                .filter(repo -> repo.getFork().equals(isFork))
                .parallel()
                .map(repository -> getRepositoryWithBranchesAsync(username, repository))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private CompletableFuture<Repository> getRepositoryWithBranchesAsync(String username, GithubRepository githubRepository) {
        return CompletableFuture.supplyAsync(() -> getRepositoryWithBranches(username, githubRepository));
    }

    private Repository getRepositoryWithBranches(String username, GithubRepository githubRepository) {
        Repository repository = githubRepositoryMapper.map(githubRepository);
        repository.setBranches(branchService.getBranchesByRepositoryAndUsername(repository.getName(), username));
        return repository;
    }
}

package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubCommit;
import com.tui.client.response.GithubOwner;
import com.tui.client.response.GithubRepository;
import com.tui.model.Branch;
import com.tui.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultRepositoryService implements RepositoryService {

    private final GithubClient githubClient;

    @Override
    public List<Repository> getRepositoriesByUsername(String username) {

        return githubClient.getRepositoriesByUsername(username).stream()
                .filter(repo -> !repo.getFork())
                .parallel()
                .map(repository -> convert(username, repository))
                .collect(Collectors.toList());
    }

    private Repository convert(String username, GithubRepository repository) {
        Repository repo = new Repository();
        repo.setName(repository.getName());
        repo.setOwnerLogin(Optional.ofNullable(repository.getOwner())
                .map(GithubOwner::getLogin).orElse(null));
        repo.setBranches(githubClient.getBranchesByRepositoryAndUserName(repository.getName(), username).stream()
                .map(this::convert)
                .collect(Collectors.toList()));
        return repo;
    }

    private Branch convert(GithubBranch branch) {
        return new Branch(branch.getName(),
                Optional.ofNullable(branch.getCommit())
                        .map(GithubCommit::getSha)
                        .orElse(null));
    }
}

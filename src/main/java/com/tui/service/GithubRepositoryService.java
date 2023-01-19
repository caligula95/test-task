package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.client.response.GithubRepository;
import com.tui.component.GithubConverter;
import com.tui.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubRepositoryService implements RepositoryService {

    private final GithubClient githubClient;
    private final GithubConverter githubConverter;

    @Override
    public List<Repository> getNonForkRepositoriesByUsername(String username) {

        return githubClient.getRepositoriesByUsername(username).stream()
                .filter(repo -> !repo.getFork())
                .parallel()
                .map(repository -> getRepositoryWithBranches(username, repository))
                .collect(Collectors.toList());
    }

    private Repository getRepositoryWithBranches(String username, GithubRepository githubRepository) {
        Repository repository = githubConverter.convert(githubRepository);
        repository.setBranches(githubClient.getBranchesByRepositoryAndUserName(githubRepository.getName(), username).stream()
                .map(githubConverter::convert)
                .collect(Collectors.toList()));
        return repository;
    }
}

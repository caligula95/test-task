package com.tui.mapper;

import com.tui.client.response.GithubOwner;
import com.tui.client.response.GithubRepository;
import com.tui.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GithubRepositoryMapper {

    /**
     * Maps {@link GithubRepository} to {@link Repository}
     *
     * @param githubRepository {@link GithubRepository}
     * @return {@link Repository}
     */
    public Repository map(GithubRepository githubRepository) {
        Repository repository = new Repository();
        repository.setName(githubRepository.getName());
        repository.setOwnerLogin(Optional.ofNullable(githubRepository.getOwner())
                .map(GithubOwner::getLogin)
                .orElse(null));
        return repository;
    }
}

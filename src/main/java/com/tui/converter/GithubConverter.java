package com.tui.converter;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubCommit;
import com.tui.client.response.GithubOwner;
import com.tui.client.response.GithubRepository;
import com.tui.model.Branch;
import com.tui.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GithubConverter {

    /**
     * Converts {@link GithubRepository} to {@link Repository}
     *
     * @param repository {@link GithubRepository}
     * @return {@link Repository}
     */
    public Repository convert(GithubRepository githubRepository) {
        Repository repository = new Repository();
        repository.setName(githubRepository.getName());
        repository.setOwnerLogin(Optional.ofNullable(githubRepository.getOwner())
                .map(GithubOwner::getLogin)
                .orElse(null));
        return repository;
    }

    /**
     * Converts {@link GithubBranch} to {@link Branch}
     *
     * @param branch {@link GithubBranch}
     * @return {@link Branch}
     */
    public Branch convert(GithubBranch githubBranch) {
        Branch branch = new Branch();
        branch.setName(githubBranch.getName());
        branch.setLastCommitSha(Optional.ofNullable(githubBranch.getCommit())
                .map(GithubCommit::getSha)
                .orElse(null));
        return branch;
    }
}

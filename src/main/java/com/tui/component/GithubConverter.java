package com.tui.component;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubCommit;
import com.tui.client.response.GithubOwner;
import com.tui.client.response.GithubRepository;
import com.tui.model.Branch;
import com.tui.model.Repository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GithubConverter {

    private final ModelMapper modelMapper;


    /**
     * Converts {@link GithubRepository} to {@link Repository}
     *
     * @param repository {@link GithubRepository}
     * @return {@link Repository}
     */
    public Repository convert(GithubRepository repository) {
        modelMapper.typeMap(GithubRepository.class, Repository.class)
                .addMappings(mapper -> mapper.map(src -> Optional.ofNullable(src.getOwner())
                                .map(GithubOwner::getLogin)
                                .orElse(null),
                        Repository::setOwnerLogin));
        return modelMapper.map(repository, Repository.class);
    }

    /**
     * Converts {@link GithubBranch} to {@link Branch}
     *
     * @param branch {@link GithubBranch}
     * @return {@link Branch}
     */
    public Branch convert(GithubBranch branch) {
        modelMapper.typeMap(GithubBranch.class, Branch.class)
                .addMappings(mapper -> mapper.map(src -> Optional.ofNullable(src.getCommit())
                                .map(GithubCommit::getSha)
                                .orElse(null),
                        Branch::setLastCommitSha));
        return modelMapper.map(branch, Branch.class);
    }
}

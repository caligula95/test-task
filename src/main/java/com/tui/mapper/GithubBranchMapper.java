package com.tui.mapper;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubCommit;
import com.tui.model.Branch;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GithubBranchMapper {

    /**
     * Maps {@link GithubBranch} to {@link Branch}
     *
     * @param githubBranch {@link GithubBranch}
     * @return {@link Branch}
     */
    public Branch map(GithubBranch githubBranch) {
        Branch branch = new Branch();
        branch.setName(githubBranch.getName());
        branch.setLastCommitSha(Optional.ofNullable(githubBranch.getCommit())
                .map(GithubCommit::getSha)
                .orElse(null));
        return branch;
    }
}

package com.tui.service;

import com.tui.client.GithubClient;
import com.tui.client.response.GithubBranch;
import com.tui.mapper.GithubBranchMapper;
import com.tui.model.Branch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubBranchService implements BranchService {

    private final GithubClient githubClient;
    private final GithubBranchMapper githubBranchMapper;

    @Override
    public List<Branch> getBranchesByRepositoryAndUsername(String repository, String username) {
        int page = 1;
        int pageSize = 100;
        List<GithubBranch> githubResponse = githubClient.getBranchesByRepositoryAndUserName(repository, username, page, pageSize);

        List<GithubBranch> allBranches = new ArrayList<>();
        while (githubResponse != null && !githubResponse.isEmpty()) {
            allBranches.addAll(githubResponse);
            githubResponse = githubClient.getBranchesByRepositoryAndUserName(repository, username, ++page, pageSize);
        }

        return allBranches.stream().map(githubBranchMapper::map)
                .collect(Collectors.toList());
    }
}

package com.tui.prototype;

import com.tui.client.response.GithubBranch;
import com.tui.client.response.GithubCommit;
import com.tui.client.response.GithubOwner;
import com.tui.client.response.GithubRepository;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GithubResponsePrototype {

    public static GithubBranch aGithubBranch() {
        GithubBranch githubBranch = new GithubBranch();
        githubBranch.setName("name");
        githubBranch.setCommit(aGithubCommit());
        return githubBranch;
    }

    public static GithubCommit aGithubCommit() {
        GithubCommit githubCommit = new GithubCommit();
        githubCommit.setSha("testSha");
        return githubCommit;
    }

    public static GithubRepository aGithubRepository(boolean isFork) {
        GithubRepository githubRepository = new GithubRepository();
        githubRepository.setName("repoName" + isFork);
        githubRepository.setFork(isFork);
        githubRepository.setOwner(aGithubOwner());
        return githubRepository;
    }

    public static GithubOwner aGithubOwner() {
        GithubOwner githubOwner = new GithubOwner();
        githubOwner.setLogin("ownerLogin");
        return githubOwner;
    }
}

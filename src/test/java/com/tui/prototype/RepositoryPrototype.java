package com.tui.prototype;

import com.tui.model.Branch;
import com.tui.model.Repository;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RepositoryPrototype {

    public static Repository aRepository() {
        Repository repository = new Repository();
        repository.setName("repoName");
        repository.setOwnerLogin("ownerLogin");
        repository.setBranches(List.of(aBranch(), aBranch()));
        return repository;
    }

    public static Branch aBranch() {
        return new Branch("branchName", "lastCommitSha");
    }
}

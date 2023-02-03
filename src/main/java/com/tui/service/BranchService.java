package com.tui.service;

import com.tui.model.Branch;

import java.util.List;

public interface BranchService {

    List<Branch> getBranchesByRepositoryAndUsername(String repository, String username);
}

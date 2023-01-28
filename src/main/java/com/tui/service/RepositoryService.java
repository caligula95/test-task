package com.tui.service;

import com.tui.model.Repository;

import java.util.List;

/**
 * Service responsible for repositories internal logic
 *
 * @author denysburda
 */
public interface RepositoryService {

    /**
     * Method responsible for getting owner fork/non-fork repos along with branches
     *
     * @param username repositories owner
     * @param isFork
     * @return {@link Repository}
     */
    List<Repository> getRepositoriesByUsernameAndForkParam(String username, boolean isFork);
}

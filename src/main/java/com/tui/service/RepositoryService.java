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
     * Method responsible for getting owner non forked repos along with branches
     *
     * @param username repositories owner
     * @return {@link Repository}
     */
    List<Repository> getNonForkRepositoriesByUsername(String username);
}

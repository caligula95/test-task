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
     * Method responsible for getting owner repos along with branches
     *
     * @param username repositories owner
     * @return list of com.tui.model.Repository
     */
    List<Repository> getRepositoriesByUsername(String username);
}

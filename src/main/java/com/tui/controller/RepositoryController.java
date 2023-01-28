package com.tui.controller;

import com.tui.model.Repository;
import com.tui.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller responsible for repositories requests
 *
 * @author denysburda
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class RepositoryController {

    private final RepositoryService repositoryService;

    @GetMapping(value = "/users/{username}/repositories", headers = ACCEPT + "=" + APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Repository>> getRepositories(@PathVariable String username) {
        log.info("Handling get user repositories request with username: {}", username);
        return ResponseEntity.ok(repositoryService.getRepositoriesByUsernameAndForkParam(username, false));
    }
}

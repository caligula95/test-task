package com.tui.controller;

import com.tui.model.Repository;
import com.tui.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller responsible for repositories requests
 *
 * @author denysburda
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/repositories")
@Slf4j
public class RepositoryController {

    private final RepositoryService repositoryService;

    @GetMapping(headers = HttpHeaders.ACCEPT + "=" + MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Repository>> getRepositories(@RequestParam String username) {
        log.info("Handling get user repositories request with username: {}", username);
        return ResponseEntity.ok(repositoryService.getRepositoriesByUsername(username));
    }
}

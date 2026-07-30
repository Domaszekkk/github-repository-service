package com.example.githubrepositoryservice.controller;

import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.entity.Repository;
import com.example.githubrepositoryservice.service.GitHubRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
@Tag(name = "GitHub Repository", description = "Fetching repository details from GitHub")
public class GitHubRepositoryController {

    private final GitHubRepositoryService gitHubRepositoryService;

    @GetMapping("/{owner}/{repository-name}")
    @Operation(summary = "Get repository details", description = "Fetches repository details from GitHub API")
    @ApiResponse(responseCode = "200", description = "Repository found and returned")
    @ApiResponse(responseCode = "404", description = "Repository not found on GitHub")
    public RepositoryResponse getRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        return gitHubRepositoryService.getRepository(owner, repositoryName);
    }

    @PostMapping("/{owner}/{repository-name}")
    @Operation(summary = "Fetch and save repository details", description = "Fetches repository details from GitHub API and saves them in the local database")
    @ApiResponse(responseCode = "200", description = "Repository fetched and saved successfully")
    @ApiResponse(responseCode = "404", description = "Repository not found on GitHub")
    public RepositoryResponse saveRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        return gitHubRepositoryService.saveRepository(owner, repositoryName);
    }

    @PutMapping
    public RepositoryResponse updatedRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        return gitHubRepositoryService.updateRepository(owner, repositoryName);
    }
}
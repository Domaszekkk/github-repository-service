package com.example.githubrepositoryservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/local/repositories")
@RequiredArgsConstructor
@Tag(name = "Local Repository", description = "Reading repository details from local database")
public class LocalRepositoryController {
    private final GitHubRepositoryService gitHubRepositoryService;
    private final RepositoryMapper mapper;

    @GetMapping("/{owner}/{repository-name}")
    @Operation(summary = "Get repository from local database", description = "Fetches previously saved repository details from the local database")
    @ApiResponse(responseCode = "200", description = "Repository found and returned")
    @ApiResponse(responseCode = "404", description = "Repository not found in local database")
    public RepositoryResponse getRepositoryFromLocal(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        Repository repository = gitHubRepositoryService.getRepositoryFromLocal(owner, repositoryName);
        return mapper.toResponse(repository);
    }
}
package com.example.githubrepositoryservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
@Tag(name = "GitHub Repository", description = "Fetching repository details from GitHub")
public class GitHubRepositoryController {
    private final GitHubRepositoryService gitHubRepositoryService;
    private final RepositoryMapper mapper;

    @GetMapping("/{owner}/{repository-name}")
    @Operation(summary = "Get repository details", description = "Fetches repository details from GitHub API")
    @ApiResponse(responseCode = "200", description = "Repository found and returned")
    @ApiResponse(responseCode = "404", description = "Repository not found on GitHub")
    public RepositoryResponse getRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        Repository repository = gitHubRepositoryService.getRepository(owner, repositoryName);
        return mapper.toResponse(repository);
    }

    @PostMapping("/{owner}/{repository-name}")
    @Operation(summary = "Fetch and save repository details", description = "Fetches repository details from GitHub API and saves them in the local database")
    @ApiResponse(responseCode = "200", description = "Repository fetched and saved successfully")
    @ApiResponse(responseCode = "404", description = "Repository not found on GitHub")
    public RepositoryResponse saveRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        Repository repository = gitHubRepositoryService.saveRepository(owner, repositoryName);
        return mapper.toResponse(repository);
    }

    @PutMapping("/{owner}/{repository-name}")
    @Operation(summary = "Update repository details", description = "Fetches latest repository details from GitHub API and updates the local database entry")
    @ApiResponse(responseCode = "200", description = "Repository updated successfully")
    @ApiResponse(responseCode = "404", description = "Repository not found on GitHub or not found in local database")
    public RepositoryResponse updatedRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        Repository repository = gitHubRepositoryService.updateRepository(owner, repositoryName);
        return mapper.toResponse(repository);
    }

    @DeleteMapping("/{owner}/{repository-name}")
    @Operation(summary = "Delete repository details", description = "Deletes repository details from the local database")
    @ApiResponse(responseCode = "204", description = "Repository deleted successfully")
    @ApiResponse(responseCode = "404", description = "Repository not found in local database")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRepository(@PathVariable String owner, @PathVariable("repository-name") String repositoryName) {
        gitHubRepositoryService.deleteRepository(owner, repositoryName);
    }
}
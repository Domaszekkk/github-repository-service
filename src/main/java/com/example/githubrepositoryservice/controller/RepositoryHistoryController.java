package com.example.githubrepositoryservice.controller;

import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.dto.PageResponse;
import com.example.githubrepositoryservice.service.GitHubRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
@Tag(name = "Repository History", description = "Browsing previously fetched repositories")
public class RepositoryHistoryController {
    private final GitHubRepositoryService gitHubRepositoryService;

    @GetMapping
    @Operation(summary = "Get repository history", description = "Returns a paginated list of previously fetched repositories")
    @ApiResponse(responseCode = "200", description = "History returned successfully")
    public PageResponse<RepositoryResponse> getRepositoryHistory(@ParameterObject Pageable pageable) {
        return gitHubRepositoryService.getRepositoryHistory(pageable);
    }
}
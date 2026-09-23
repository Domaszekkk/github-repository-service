package com.example.githubrepositoryservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
@Tag(name = "Repository History", description = "Browsing previously fetched repositories")
public class RepositoryHistoryController {
    private final GitHubRepositoryService gitHubRepositoryService;
    private final RepositoryMapper mapper;

    @GetMapping
    @Operation(summary = "Get repository history", description = "Returns a paginated list of previously fetched repositories")
    @ApiResponse(responseCode = "200", description = "History returned successfully")
    public PageResponse<RepositoryResponse> getRepositoryHistory(
            @ParameterObject org.springframework.data.domain.Pageable springPageable) {
        Pageable pageable = Pageable.builder()
                .page(springPageable.getPageNumber())
                .size(springPageable.getPageSize())
                .build();
        Page<Repository> page = gitHubRepositoryService.getRepositoryHistory(pageable);
        return mapper.toResponsePage(page);
    }
}
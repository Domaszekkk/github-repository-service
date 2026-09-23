package com.example.githubrepositoryservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    @Mapping(target = "stars", source = "stargazersCount")
    @Mapping(target = "fetchedAt", ignore = true)
    RepositoryEntity toEntity(Repository repository);

    @Mapping(target = "stargazersCount", source = "stars")
    Repository toModel(RepositoryEntity entity);

    Page<Repository> toModelPage(org.springframework.data.domain.Page<RepositoryEntity> entityPage);

    @Mapping(target = "id", ignore = true)
    Repository toModel(GitHubRepositoryResponse response);

    @Mapping(target = "stars", source = "stargazersCount")
    RepositoryResponse toResponse(Repository repository);

    PageResponse<RepositoryResponse> toResponsePage(Page<Repository> page);
}

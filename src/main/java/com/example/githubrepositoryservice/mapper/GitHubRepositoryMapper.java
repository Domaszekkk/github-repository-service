package com.example.githubrepositoryservice.mapper;

import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.entity.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = Instant.class)
public interface GitHubRepositoryMapper {
    @Mapping(source = "stargazersCount", target = "stars")
    RepositoryResponse toResponse(GitHubRepositoryDto dto);

    @Mapping(source = "dto.stargazersCount", target = "stars")
    @Mapping(target = "fetchedAt", expression = "java(Instant.now())")
    Repository toEntity(GitHubRepositoryDto dto);

    RepositoryResponse toResponseFromEntity(Repository entity);
}
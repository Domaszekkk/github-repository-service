package com.example.githubrepositoryservice;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record GitHubRepositoryResponse(@JsonProperty("full_name") String fullName,
                                       @JsonProperty("description") String description,
                                       @JsonProperty("clone_url") String cloneUrl,
                                       @JsonProperty("stargazers_count") int stargazersCount,
                                       @JsonProperty("created_at") Instant createdAt) {
}

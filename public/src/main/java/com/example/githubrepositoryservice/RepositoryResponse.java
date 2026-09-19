package com.example.githubrepositoryservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryResponse {
    @JsonProperty("fullName")
    private String fullName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("cloneUrl")
    private String cloneUrl;
    @JsonProperty("stars")
    private int stars;
    @JsonProperty("createdAt")
    private Instant createdAt;
}

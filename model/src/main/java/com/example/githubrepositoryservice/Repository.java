package com.example.githubrepositoryservice;

import lombok.*;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Repository {
    private String fullName;
    private String description;
    private String cloneUrl;
    private int stargazersCount;
    private Instant createdAt;
}

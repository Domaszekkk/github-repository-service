package com.example.githubrepositoryservice;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Repository {
    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private int stargazersCount;
    private Instant createdAt;
}

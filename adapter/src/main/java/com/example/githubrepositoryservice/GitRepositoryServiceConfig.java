package com.example.githubrepositoryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitRepositoryServiceConfig {

    @Bean
    GitHubRepositoryService GitHubRepositoryService {
        return new GitHubRepositoryService()
    }
}

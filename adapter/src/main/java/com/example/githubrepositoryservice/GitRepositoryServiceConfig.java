package com.example.githubrepositoryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitRepositoryServiceConfig {

    @Bean
    GitHubRepositoryService gitHubRepositoryService(
            GitHubApiProviderPort gitHubApiProviderPort,
            RepositoryProviderPort repositoryProviderPort,
            RepositoryValidatorPort repositoryValidatorPort) {
        return new GitHubRepositoryService(
                gitHubApiProviderPort,
                repositoryProviderPort,
                repositoryValidatorPort);
    }
}

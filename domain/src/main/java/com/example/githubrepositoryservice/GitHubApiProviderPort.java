package com.example.githubrepositoryservice;

public interface GitHubApiProviderPort {
    Repository getRepository(String owner, String repositoryName);
}

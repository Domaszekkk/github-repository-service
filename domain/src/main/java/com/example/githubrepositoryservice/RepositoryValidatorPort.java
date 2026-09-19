package com.example.githubrepositoryservice;

public interface RepositoryValidatorPort {
    void validateNotExists(String owner, String repositoryName);
}

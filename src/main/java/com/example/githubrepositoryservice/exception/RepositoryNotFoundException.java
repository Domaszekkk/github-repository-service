package com.example.githubrepositoryservice.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GithubRepositoryException {
    public RepositoryNotFoundException(String owner, String repositoryName) {
        super("Repository not found: " + owner + "/" + repositoryName, HttpStatus.NOT_FOUND);
    }
}
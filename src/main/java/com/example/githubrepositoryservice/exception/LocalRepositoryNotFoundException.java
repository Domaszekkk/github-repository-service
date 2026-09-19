package com.example.githubrepositoryservice.exception;

import org.springframework.http.HttpStatus;

public class LocalRepositoryNotFoundException extends GithubRepositoryException {

    public LocalRepositoryNotFoundException(String owner, String repositoryName) {
        super("Repository not found in local database: " + owner + "/" + repositoryName, HttpStatus.NOT_FOUND);
    }
}
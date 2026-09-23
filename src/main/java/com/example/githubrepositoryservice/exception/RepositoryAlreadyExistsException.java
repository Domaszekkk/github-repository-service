package com.example.githubrepositoryservice.exception;

import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExistsException extends GithubRepositoryException {
    public RepositoryAlreadyExistsException(String owner, String repositoryName) {
        super("Repository already exists in local database: " + owner + "/" + repositoryName, HttpStatus.CONFLICT);
    }
}
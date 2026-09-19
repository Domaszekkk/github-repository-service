package com.example.githubrepositoryservice.exception;

import org.springframework.http.HttpStatus;

public class GithubServiceUnavailableException extends GithubRepositoryException {

    public GithubServiceUnavailableException() {
        super("GitHub API is currently unavailable, please try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
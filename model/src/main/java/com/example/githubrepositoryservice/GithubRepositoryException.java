package com.example.githubrepositoryservice;

import lombok.Getter;

@Getter
public class GithubRepositoryException extends RuntimeException {
    private final Integer responseCode;

    public GithubRepositoryException(String message, Integer responseCode) {
        super(message);
        this.responseCode = responseCode;
    }
}
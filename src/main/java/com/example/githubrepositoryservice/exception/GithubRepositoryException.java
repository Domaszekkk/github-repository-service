package com.example.githubrepositoryservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubRepositoryException extends RuntimeException {
    private final HttpStatus status;

    public GithubRepositoryException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
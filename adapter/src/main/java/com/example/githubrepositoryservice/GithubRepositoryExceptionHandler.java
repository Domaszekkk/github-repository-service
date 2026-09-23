package com.example.githubrepositoryservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GithubRepositoryExceptionHandler {
    @ExceptionHandler(GithubRepositoryException.class)
    public ResponseEntity<ErrorMessage> handleGithubRepositoryException(GithubRepositoryException exception) {
        return ResponseEntity
                .status(exception.getResponseCode())
                .body(new ErrorMessage(exception.getMessage()));
    }
}
package com.example.githubrepositoryservice;


public class LocalRepositoryNotFoundException extends GithubRepositoryException {

    public LocalRepositoryNotFoundException(String owner, String repositoryName) {
        super("Repository not found in local database: " + owner + "/" + repositoryName, 404);
    }
}
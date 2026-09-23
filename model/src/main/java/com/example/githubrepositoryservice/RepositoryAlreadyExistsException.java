package com.example.githubrepositoryservice;

public class RepositoryAlreadyExistsException extends GithubRepositoryException {
    public RepositoryAlreadyExistsException(String owner, String repositoryName) {
        super("Repository already exists: " + owner + "/" + repositoryName, 409);
    }
}

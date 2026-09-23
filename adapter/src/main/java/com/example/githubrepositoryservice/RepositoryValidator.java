package com.example.githubrepositoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryValidator implements RepositoryValidatorPort {
    private final GitHubRepositoryJpa gitHubRepositoryJpa;

    public void validateNotExists(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        if (gitHubRepositoryJpa.findByFullName(fullName).isPresent()) {
            throw new RepositoryAlreadyExistsException(owner, repositoryName);
        }
    }
}
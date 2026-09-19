package com.example.githubrepositoryservice;

import com.example.githubrepositoryservice.exception.RepositoryAlreadyExistsException;
import com.example.githubrepositoryservice.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryValidator {
    private final RepositoryProviderPort repositoryProviderPort;
    public void validateNotExists(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        if (repositoryJpaRepository.findByFullName(fullName).isPresent()) {
            throw new RepositoryAlreadyExistsException(owner, repositoryName);
        }
    }
}
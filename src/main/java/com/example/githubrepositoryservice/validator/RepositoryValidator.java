package com.example.githubrepositoryservice.validator;

import com.example.githubrepositoryservice.exception.RepositoryAlreadyExistsException;
import com.example.githubrepositoryservice.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryValidator {
    private final RepositoryJpaRepository repositoryJpaRepository;
    public void validateNotExists(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        if (repositoryJpaRepository.findByFullName(fullName).isPresent()) {
            throw new RepositoryAlreadyExistsException(owner, repositoryName);
        }
    }
}
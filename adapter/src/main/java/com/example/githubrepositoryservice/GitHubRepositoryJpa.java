package com.example.githubrepositoryservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GitHubRepositoryJpa extends JpaRepository<RepositoryEntity, Long> {
    Optional<RepositoryEntity> findByFullName(String fullName);
    void deleteByFullName(String fullName);
}

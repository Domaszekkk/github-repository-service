package com.example.githubrepositoryservice.repository;

import com.example.githubrepositoryservice.entity.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryJpaRepository extends JpaRepository<Repository, Long> {
    Page<Repository> findAll(Pageable pageable);
    Optional<Repository> findByFullName(String fullName);
    void deleteByFullName(String fullName);
}
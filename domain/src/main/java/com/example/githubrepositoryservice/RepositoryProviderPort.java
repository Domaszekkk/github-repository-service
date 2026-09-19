package com.example.githubrepositoryservice;

import java.util.List;
import java.util.Optional;

public interface RepositoryProviderPort {
    Optional <Repository> findByFullName(String fullName);

    Page<Repository> findAll(Pageable pageable);

    Repository save(Repository repository);

    void deleteByFullName(String fullName);
}

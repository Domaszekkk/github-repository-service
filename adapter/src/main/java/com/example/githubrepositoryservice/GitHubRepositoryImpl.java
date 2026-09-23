package com.example.githubrepositoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GitHubRepositoryImpl implements RepositoryProviderPort {
    private final GitHubRepositoryJpa gitHubRepositoryJpa;
    private final RepositoryMapper mapper;

    @Override
    public Optional<Repository> findByFullName(String fullName) {
        return gitHubRepositoryJpa.findByFullName(fullName)
                .map(mapper::toModel);
    }

    @Override
    public Page<Repository> findAll(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize());
        return mapper.toModelPage(gitHubRepositoryJpa.findAll(pageRequest));
    }

    @Override
    public Repository save(Repository repository) {
        RepositoryEntity entity = mapper.toEntity(repository);
        entity.setFetchedAt(Instant.now());
        RepositoryEntity saved = gitHubRepositoryJpa.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public void deleteByFullName(String fullName) {
        gitHubRepositoryJpa.deleteByFullName(fullName);
    }
}

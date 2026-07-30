package com.example.githubrepositoryservice.service;

import com.example.githubrepositoryservice.client.GitHubApiClient;
import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.dto.PageResponse;
import com.example.githubrepositoryservice.entity.Repository;
import com.example.githubrepositoryservice.exception.GithubServiceUnavailableException;
import com.example.githubrepositoryservice.exception.LocalRepositoryNotFoundException;
import com.example.githubrepositoryservice.exception.RepositoryNotFoundException;
import com.example.githubrepositoryservice.mapper.GitHubRepositoryMapper;
import com.example.githubrepositoryservice.repository.RepositoryJpaRepository;
import com.example.githubrepositoryservice.validator.RepositoryValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GitHubRepositoryService {
    private final GitHubApiClient gitHubApiClient;
    private final GitHubRepositoryMapper gitHubRepositoryMapper;
    private final RepositoryJpaRepository repositoryJpaRepository;
    private final RepositoryValidator repositoryValidator;

    public RepositoryResponse getRepository(String owner, String repositoryName) {
        GitHubRepositoryDto dto = fetchFromGitHub(owner, repositoryName);
        return gitHubRepositoryMapper.toResponse(dto);
    }

    public RepositoryResponse saveRepository(String owner, String repositoryName) {
        repositoryValidator.validateNotExists(owner, repositoryName);
        GitHubRepositoryDto dto = fetchFromGitHub(owner, repositoryName);
        Repository entity = gitHubRepositoryMapper.toEntity(dto);
        Repository saved = repositoryJpaRepository.save(entity);
        return gitHubRepositoryMapper.toResponseFromEntity(saved);
    }

    public PageResponse<RepositoryResponse> getRepositoryHistory(Pageable pageable) {
        Page<RepositoryResponse> page = repositoryJpaRepository.findAll(pageable)
                .map(gitHubRepositoryMapper::toResponseFromEntity);
        return PageResponse.from(page);
    }

    public RepositoryResponse getRepositoryFromLocal(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        Repository entity = repositoryJpaRepository.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        return gitHubRepositoryMapper.toResponseFromEntity(entity);
    }

    private GitHubRepositoryDto fetchFromGitHub(String owner, String repositoryName) {
        try {
            return gitHubApiClient.getRepository(owner, repositoryName);
        } catch (FeignException.NotFound e) {
            throw new RepositoryNotFoundException(owner, repositoryName);
        } catch (FeignException.ServiceUnavailable e) {
            throw new GithubServiceUnavailableException();
        }
    }

    @Transactional
    public RepositoryResponse updateRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        Repository existing = repositoryJpaRepository.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        GitHubRepositoryDto dto = fetchFromGitHub(owner, repositoryName);
        Repository updated = gitHubRepositoryMapper.toEntity(dto);
        updated.setId(existing.getId());
        repositoryJpaRepository.save(updated);
        return gitHubRepositoryMapper.toResponse(dto);
    }

    @Transactional
    public void deleteRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        repositoryJpaRepository.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        repositoryJpaRepository.deleteByFullName(fullName);
    }
}
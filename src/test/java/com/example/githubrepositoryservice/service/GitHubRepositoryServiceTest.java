package com.example.githubrepositoryservice.service;

import com.example.githubrepositoryservice.client.GitHubApiClient;
import com.example.githubrepositoryservice.dto.GitHubRepositoryDto;
import com.example.githubrepositoryservice.dto.RepositoryResponse;
import com.example.githubrepositoryservice.dto.PageResponse;
import com.example.githubrepositoryservice.entity.Repository;
import com.example.githubrepositoryservice.exception.GithubServiceUnavailableException;
import com.example.githubrepositoryservice.exception.LocalRepositoryNotFoundException;
import com.example.githubrepositoryservice.exception.RepositoryAlreadyExistsException;
import com.example.githubrepositoryservice.exception.RepositoryNotFoundException;
import com.example.githubrepositoryservice.mapper.GitHubRepositoryMapper;
import com.example.githubrepositoryservice.repository.RepositoryJpaRepository;
import com.example.githubrepositoryservice.validator.RepositoryValidator;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GitHubRepositoryServiceTest {
    private GitHubApiClient gitHubApiClient;
    private GitHubRepositoryMapper gitHubRepositoryMapper;
    private RepositoryJpaRepository repositoryJpaRepository;
    private RepositoryValidator repositoryValidator;
    private GitHubRepositoryService gitHubRepositoryService;

    @BeforeEach
    void setup() {
        this.gitHubApiClient = Mockito.mock(GitHubApiClient.class);
        this.repositoryJpaRepository = Mockito.mock(RepositoryJpaRepository.class);
        this.gitHubRepositoryMapper = Mappers.getMapper(GitHubRepositoryMapper.class);
        this.repositoryValidator = new RepositoryValidator(repositoryJpaRepository);
        this.gitHubRepositoryService = new GitHubRepositoryService(
                gitHubApiClient, gitHubRepositoryMapper, repositoryJpaRepository, repositoryValidator);
    }

    @Test
    void getRepository_DataCorrect_ReturnRepository() {
        // given
        Instant createdAt = Instant.parse("2011-01-26T19:01:12Z");
        GitHubRepositoryDto dto = new GitHubRepositoryDto(
                "octocat/Hello-World", "My first repository",
                "https://github.com/octocat/Hello-World.git", 1500, createdAt);

        when(gitHubApiClient.getRepository("octocat", "Hello-World")).thenReturn(dto);

        // when
        RepositoryResponse result = gitHubRepositoryService.getRepository("octocat", "Hello-World");

        // then
        assertAll(
                () -> assertEquals("octocat/Hello-World", result.getFullName()),
                () -> assertEquals("My first repository", result.getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getCloneUrl()),
                () -> assertEquals(1500, result.getStars()),
                () -> assertEquals(createdAt, result.getCreatedAt())
        );
        verify(repositoryJpaRepository, never()).save(any());
    }

    @Test
    void getRepository_RepositoryNotFound_ThrowsRepositoryNotFoundException() {
        // given
        Request request = Request.create(Request.HttpMethod.GET, "/repos/octocat/missing",
                Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null, Collections.emptyMap());

        when(gitHubApiClient.getRepository("octocat", "missing")).thenThrow(notFound);

        // when + then
        RepositoryNotFoundException exception = Assertions.assertThrows(
                RepositoryNotFoundException.class,
                () -> gitHubRepositoryService.getRepository("octocat", "missing"));

        assertEquals("Repository not found: octocat/missing", exception.getMessage());
    }

    @Test
    void getRepository_GithubUnavailable_ThrowsGithubServiceUnavailableException() {
        // given
        Request request = Request.create(Request.HttpMethod.GET, "/repos/octocat/Hello-World",
                Collections.emptyMap(), null, new RequestTemplate());
        FeignException.ServiceUnavailable serviceUnavailable = new FeignException.ServiceUnavailable(
                "Service Unavailable", request, null, Collections.emptyMap());

        when(gitHubApiClient.getRepository("octocat", "Hello-World")).thenThrow(serviceUnavailable);

        // when + then
        GithubServiceUnavailableException exception = Assertions.assertThrows(
                GithubServiceUnavailableException.class,
                () -> gitHubRepositoryService.getRepository("octocat", "Hello-World"));

        assertEquals("GitHub API is currently unavailable, please try again later", exception.getMessage());
    }

    @Test
    void saveRepository_DataCorrect_ReturnRepository() {
        // given
        Instant createdAt = Instant.parse("2011-01-26T19:01:12Z");
        GitHubRepositoryDto dto = new GitHubRepositoryDto(
                "octocat/Hello-World", "My first repository",
                "https://github.com/octocat/Hello-World.git", 1500, createdAt);

        Repository saved = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(createdAt)
                .fetchedAt(Instant.now())
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.empty());
        when(gitHubApiClient.getRepository("octocat", "Hello-World")).thenReturn(dto);
        when(repositoryJpaRepository.save(any(Repository.class))).thenReturn(saved);

        // when
        RepositoryResponse result = gitHubRepositoryService.saveRepository("octocat", "Hello-World");

        // then
        assertAll(
                () -> assertEquals("octocat/Hello-World", result.getFullName()),
                () -> assertEquals("My first repository", result.getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getCloneUrl()),
                () -> assertEquals(1500, result.getStars()),
                () -> assertEquals(createdAt, result.getCreatedAt())
        );
        verify(repositoryJpaRepository).save(any(Repository.class));
    }

    @Test
    void saveRepository_RepositoryAlreadyExists_ThrowsRepositoryAlreadyExistsException() {
        // given
        Repository existing = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.of(existing));

        // when + then
        RepositoryAlreadyExistsException exception = Assertions.assertThrows(
                RepositoryAlreadyExistsException.class,
                () -> gitHubRepositoryService.saveRepository("octocat", "Hello-World"));

        assertEquals("Repository already exists in local database: octocat/Hello-World", exception.getMessage());
        verify(gitHubApiClient, never()).getRepository(any(), any());
        verify(repositoryJpaRepository, never()).save(any());
    }

    @Test
    void saveRepository_RepositoryNotFound_ThrowsRepositoryNotFoundException() {
        // given
        Request request = Request.create(Request.HttpMethod.GET, "/repos/octocat/missing",
                Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null, Collections.emptyMap());

        when(repositoryJpaRepository.findByFullName("octocat/missing")).thenReturn(Optional.empty());
        when(gitHubApiClient.getRepository("octocat", "missing")).thenThrow(notFound);

        // when + then
        RepositoryNotFoundException exception = Assertions.assertThrows(
                RepositoryNotFoundException.class,
                () -> gitHubRepositoryService.saveRepository("octocat", "missing"));

        assertEquals("Repository not found: octocat/missing", exception.getMessage());
        verify(repositoryJpaRepository, never()).save(any());
    }

    @Test
    void getRepositoryHistory_DataCorrect_ReturnHistory() {
        // given
        Repository entity = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .fetchedAt(Instant.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Repository> repositoryPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(repositoryJpaRepository.findAll(pageable)).thenReturn(repositoryPage);

        // when
        PageResponse<RepositoryResponse> result = gitHubRepositoryService.getRepositoryHistory(pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("octocat/Hello-World", result.getContent().get(0).getFullName()),
                () -> assertEquals("My first repository", result.getContent().get(0).getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getContent().get(0).getCloneUrl()),
                () -> assertEquals(1500, result.getContent().get(0).getStars()),
                () -> assertEquals(Instant.parse("2011-01-26T19:01:12Z"), result.getContent().get(0).getCreatedAt()),
                () -> assertEquals(0, result.getPageNumber()),
                () -> assertEquals(10, result.getPageSize()),
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(true, result.isLast())
        );
    }

    @Test
    void getRepositoryFromLocal_DataCorrect_ReturnRepository() {
        // given
        Repository entity = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .description("My first repository")
                .cloneUrl("https://github.com/octocat/Hello-World.git")
                .stars(1500)
                .createdAt(Instant.parse("2011-01-26T19:01:12Z"))
                .fetchedAt(Instant.now())
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.of(entity));

        // when
        RepositoryResponse result = gitHubRepositoryService.getRepositoryFromLocal("octocat", "Hello-World");

        // then
        assertAll(
                () -> assertEquals("octocat/Hello-World", result.getFullName()),
                () -> assertEquals("My first repository", result.getDescription()),
                () -> assertEquals("https://github.com/octocat/Hello-World.git", result.getCloneUrl()),
                () -> assertEquals(1500, result.getStars()),
                () -> assertEquals(Instant.parse("2011-01-26T19:01:12Z"), result.getCreatedAt())
        );
    }

    @Test
    void getRepositoryFromLocal_RepositoryNotFound_ThrowsLocalRepositoryNotFoundException() {
        // given
        when(repositoryJpaRepository.findByFullName("octocat/missing")).thenReturn(Optional.empty());

        // when + then
        LocalRepositoryNotFoundException exception = Assertions.assertThrows(
                LocalRepositoryNotFoundException.class,
                () -> gitHubRepositoryService.getRepositoryFromLocal("octocat", "missing"));
        assertEquals("Repository not found in local database: octocat/missing", exception.getMessage());
    }

    @Test
    void deleteRepository_DataCorrect_DeletesRepository() {
        // given
        Repository existing = Repository.builder()
                .id(1L)
                .fullName("octocat/Hello-World")
                .build();

        when(repositoryJpaRepository.findByFullName("octocat/Hello-World")).thenReturn(Optional.of(existing));

        // when
        gitHubRepositoryService.deleteRepository("octocat", "Hello-World");

        // then
        verify(repositoryJpaRepository).findByFullName("octocat/Hello-World");
        verify(repositoryJpaRepository).deleteByFullName("octocat/Hello-World");
        verifyNoMoreInteractions(repositoryJpaRepository);
    }

    @Test
    void deleteRepository_RepositoryNotFound_ThrowsLocalRepositoryNotFoundException() {
        // given
        when(repositoryJpaRepository.findByFullName("octocat/missing")).thenReturn(Optional.empty());

        // when + then
        LocalRepositoryNotFoundException exception = Assertions.assertThrows(
                LocalRepositoryNotFoundException.class,
                () -> gitHubRepositoryService.deleteRepository("octocat", "missing"));

        assertEquals("Repository not found in local database: octocat/missing", exception.getMessage());
        verify(repositoryJpaRepository, never()).deleteByFullName(any());
    }
}
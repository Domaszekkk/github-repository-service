package com.example.githubrepositoryservice;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubRepositoryService {
    private final GitHubApiProviderPort gitHubApiProviderPort;
    private final RepositoryProviderPort repositoryProviderPort;
    private final RepositoryValidatorPort repositoryValidatorPort;

    public Repository getRepository(String owner, String repositoryName) {
        Repository dto = fetchFromGitHub(owner, repositoryName);
        return;
    }

    public Repository saveRepository(String owner, String repositoryName) {
        repositoryValidatorPort.validateNotExists(owner, repositoryName);
        Repository dto = fetchFromGitHub(owner, repositoryName);
        Repository repository = new Repository();
        Repository saved = repositoryProviderPort.save(repository);
        return;
    }

    public PageResponse<Repository> getRepositoryHistory(Pageable pageable) {
        Page<Repository> page = repositoryProviderPort.findAll(pageable);
        return PageResponse.from(page);
    }

    public Repository getRepositoryFromLocal(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        Repository entity = repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        return;
    }

    private Repository fetchFromGitHub(String owner, String repositoryName) {
        try {
            return gitHubApiProviderPort.getRepository(owner, repositoryName);
        } catch (FeignException.NotFound e) {
            throw new RepositoryNotFoundException(owner, repositoryName);
        } catch (feign.RetryableException e) {
            throw new GithubServiceUnavailableException();
        }
    }

    public Repository updateRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        Repository existing = repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        Repository dto = fetchFromGitHub(owner, repositoryName);
        Repository updated = gitHubRepositoryMapper.toEntity(dto);
        updated.setId(existing.getId());
        repositoryProviderPort.save(updated);
        return;
    }

    public void deleteRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        repositoryProviderPort.deleteByFullName(fullName);
    }
}
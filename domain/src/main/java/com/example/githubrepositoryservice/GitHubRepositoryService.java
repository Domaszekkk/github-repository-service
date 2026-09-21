package com.example.githubrepositoryservice;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubRepositoryService {
    private final GitHubApiProviderPort gitHubApiProviderPort;
    private final RepositoryProviderPort repositoryProviderPort;
    private final RepositoryValidatorPort repositoryValidatorPort;

    public Repository getRepository(String owner, String repositoryName) {
        return fetchFromGitHub(owner, repositoryName);
    }

    public Repository saveRepository(String owner, String repositoryName) {
        repositoryValidatorPort.validateNotExists(owner, repositoryName);
        Repository repository = fetchFromGitHub(owner, repositoryName);
        return repositoryProviderPort.save(repository);
    }

    public Page<Repository> getRepositoryHistory(Pageable pageable) {
        return repositoryProviderPort.findAll(pageable);
    }

    public Repository getRepositoryFromLocal(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        return repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
    }

    private Repository fetchFromGitHub(String owner, String repositoryName) {
        return gitHubApiProviderPort.getRepository(owner, repositoryName);
    }

    public Repository updateRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        Repository existing = repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        Repository repository = fetchFromGitHub(owner, repositoryName);
        repository.setId(existing.getId());
        return repositoryProviderPort.save(repository);
    }

    public void deleteRepository(String owner, String repositoryName) {
        String fullName = owner + "/" + repositoryName;
        repositoryProviderPort.findByFullName(fullName)
                .orElseThrow(() -> new LocalRepositoryNotFoundException(owner, repositoryName));
        repositoryProviderPort.deleteByFullName(fullName);
    }
}
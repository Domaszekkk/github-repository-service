package com.example.githubrepositoryservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitHubApiProviderImpl implements GitHubApiProviderPort {

    private final GitHubFeignClient gitHubFeignClient;
    private final RepositoryMapper mapper;

    @Override
    public Repository getRepository(String owner, String repositoryName) {
        return mapper.toModel(gitHubFeignClient.getRepository(owner, repositoryName));
    }
}
